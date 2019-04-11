package io.micronaut.jackson.parser;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.async.ByteArrayFeeder;
import com.fasterxml.jackson.core.io.JsonEOFException;
import com.fasterxml.jackson.core.json.async.NonBlockingJsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;

import io.micronaut.core.async.processor.SingleThreadedBufferingProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * A Reactive streams publisher that publishes a {@link JsonNode} once the JSON
 * has been fully consumed. Uses
 * {@link com.fasterxml.jackson.core.json.async.NonBlockingJsonParser}
 * internally allowing the parsing of JSON from an incoming stream of bytes in a
 * non-blocking manner
 *
 * @author Graeme Rocher
 * @since 1.0
 */
public class JacksonProcessor2 extends SingleThreadedBufferingProcessor<byte[], JsonNode> {

  private static final Logger LOG = LoggerFactory.getLogger(JacksonProcessor2.class);

  private NonBlockingJsonParser currentNonBlockingJsonParser;
  private final ConcurrentLinkedDeque<JsonNode> nodeStack = new ConcurrentLinkedDeque<>();
  private final JsonFactory jsonFactory;
  private String currentFieldName;
  private boolean streamArray;

  /**
   * Creates a new JacksonProcessor.
   *
   * @param jsonFactory The JSON factory
   * @param streamArray Whether arrays should be streamed
   */
  public JacksonProcessor2(JsonFactory jsonFactory, boolean streamArray) {
    try {
      this.jsonFactory = jsonFactory;
      this.currentNonBlockingJsonParser = (NonBlockingJsonParser) jsonFactory.createNonBlockingByteArrayParser();
      this.streamArray = streamArray;
    } catch (IOException e) {
      throw new IllegalStateException("Failed to create non-blocking JSON parser: " + e.getMessage(), e);
    }
  }

  /**
   * Construct with given JSON factory.
   *
   * @param jsonFactory To configure and construct reader (aka parser,
   *                    {@link JsonParser}) and writer (aka generator,
   *                    {@link JsonGenerator}) instances.
   */
  public JacksonProcessor2(JsonFactory jsonFactory) {
    this(jsonFactory, false);
  }

  /**
   * Construct with default JSON factory.
   */
  public JacksonProcessor2() {
    this(new JsonFactory());
  }

  /**
   * @return Whether more input is needed
   */
  public boolean needMoreInput() {
    return currentNonBlockingJsonParser.getNonBlockingInputFeeder().needMoreInput();
  }

  @Override
  protected void doOnComplete() {
    if (needMoreInput()) {
      doOnError(new JsonEOFException(currentNonBlockingJsonParser, JsonToken.NOT_AVAILABLE, "Unexpected end-of-input"));
    } else {
      super.doOnComplete();
    }
  }

  @Override
  protected void onUpstreamMessage(byte[] message) {
    try {
      if (LOG.isTraceEnabled()) {
        LOG.trace("Received upstream bytes of length: " + message.length);
      }

      ByteArrayFeeder byteFeeder = currentNonBlockingJsonParser.getNonBlockingInputFeeder();
      boolean consumed = false;
      boolean needMoreInput = byteFeeder.needMoreInput();
      if (!needMoreInput) {
        currentNonBlockingJsonParser = (NonBlockingJsonParser) jsonFactory.createNonBlockingByteArrayParser();
        byteFeeder = currentNonBlockingJsonParser.getNonBlockingInputFeeder();
      }
      while (!consumed) {
        if (byteFeeder.needMoreInput()) {
          byteFeeder.feedInput(message, 0, message.length);
          consumed = true;
        }

        JsonToken event;
        while ((event = currentNonBlockingJsonParser.nextToken()) != JsonToken.NOT_AVAILABLE) {
          JsonNode root = asJsonNode(event);
          if (root != null) {

            boolean isLast = nodeStack.isEmpty();
            if (isLast) {
              byteFeeder.endOfInput();
            }

            if (isLast && streamArray && root instanceof ArrayNode) {
              break;
            } else {
              currentDownstreamSubscriber().ifPresent(subscriber -> {
                if (LOG.isTraceEnabled()) {
                  LOG.trace("Materialized new JsonNode call onNext...");
                }
                subscriber.onNext(root);
              });
            }
            if (isLast) {
              break;
            }
          }
        }
        if (needMoreInput()) {
          if (LOG.isTraceEnabled()) {
            LOG.trace("More input required to parse JSON. Demanding more.");
          }
          upstreamSubscription.request(1);
          upstreamDemand++;
        }
      }
    } catch (IOException e) {
      onError(e);
    }
  }

  private JsonNode streamArrayNodeOrAddNodeToArray(ArrayNode arrayNode, ValueNode valueNode) {
    // Only stream array elements of root array node.
    if (streamArray && nodeStack.size() == 1) {
      return valueNode;
    } else {
      arrayNode.add(valueNode);
      return null;
    }
  }
  
  /**
   * @return The root node when the whole tree is built.
   **/
  private JsonNode asJsonNode(JsonToken event) throws IOException {
    switch (event) {
    case START_OBJECT:
      nodeStack.push(node(nodeStack.peekFirst()));
      break;

    case START_ARRAY:
      nodeStack.push(array(nodeStack.peekFirst()));
      break;

    case END_OBJECT:
    case END_ARRAY:
      if (nodeStack.isEmpty()) {
        throw new JsonParseException(currentNonBlockingJsonParser, "Unexpected array end literal");
      }
      JsonNode current = nodeStack.pop();
      if (nodeStack.isEmpty()) {
        return current;
      } else {
        if (streamArray && nodeStack.size() == 1) {
          JsonNode jsonNode = nodeStack.peekFirst();
          if (jsonNode instanceof ArrayNode) {
            return current;
          } else {
            return null;
          }
        } else {
          return null;
        }
      }

    case FIELD_NAME:
      if (nodeStack.isEmpty()) {
        throw new JsonParseException(currentNonBlockingJsonParser, "Unexpected field literal");
      }
      currentFieldName = currentNonBlockingJsonParser.getCurrentName();
      break;

    case VALUE_NUMBER_INT:
      if (nodeStack.isEmpty()) {
        throw new JsonParseException(currentNonBlockingJsonParser, "Unexpected integer literal");
      }
      JsonNode intNode = nodeStack.peekFirst();
      if (intNode instanceof ObjectNode) {
        ((ObjectNode) intNode).put(currentFieldName, currentNonBlockingJsonParser.getLongValue());
      } else {
        ArrayNode arrayNode = (ArrayNode) intNode;
        ValueNode valueNode = arrayNode.numberNode(currentNonBlockingJsonParser.getLongValue());
        return streamArrayNodeOrAddNodeToArray(arrayNode, valueNode);
      }
      break;

    case VALUE_STRING:
      if (nodeStack.isEmpty()) {
        throw new JsonParseException(currentNonBlockingJsonParser, "Unexpected string literal");
      }
      JsonNode stringNode = nodeStack.peekFirst();
      if (stringNode instanceof ObjectNode) {
        ((ObjectNode) stringNode).put(currentFieldName, currentNonBlockingJsonParser.getValueAsString());
      } else {
        ArrayNode arrayNode = (ArrayNode) stringNode;
        ValueNode valueNode = arrayNode.textNode(currentNonBlockingJsonParser.getValueAsString());
        return streamArrayNodeOrAddNodeToArray(arrayNode, valueNode);
      }
      break;

    case VALUE_NUMBER_FLOAT:
      if (nodeStack.isEmpty()) {
        throw new JsonParseException(currentNonBlockingJsonParser, "Unexpected float literal");
      }

      final JsonParser.NumberType numberType = currentNonBlockingJsonParser.getNumberType();
      JsonNode decimalNode = nodeStack.peekFirst();
      switch (numberType) {

      case FLOAT:
        if (decimalNode instanceof ObjectNode) {
          ((ObjectNode) decimalNode).put(currentFieldName, currentNonBlockingJsonParser.getFloatValue());
        } else {
          ArrayNode arrayNode = (ArrayNode) decimalNode;
          ValueNode valueNode = arrayNode.numberNode(currentNonBlockingJsonParser.getFloatValue());
          return streamArrayNodeOrAddNodeToArray(arrayNode, valueNode);
        }
        break;
      case DOUBLE:
        if (decimalNode instanceof ObjectNode) {
          ((ObjectNode) decimalNode).put(currentFieldName, currentNonBlockingJsonParser.getDoubleValue());
        } else {
          ArrayNode arrayNode = (ArrayNode) decimalNode;
          ValueNode valueNode = arrayNode.numberNode(currentNonBlockingJsonParser.getDoubleValue());
          return streamArrayNodeOrAddNodeToArray(arrayNode, valueNode);
        }
        break;
      case BIG_DECIMAL:
        if (decimalNode instanceof ObjectNode) {
          ((ObjectNode) decimalNode).put(currentFieldName, currentNonBlockingJsonParser.getDecimalValue());
        } else {
          ArrayNode arrayNode = (ArrayNode) decimalNode;
          ValueNode valueNode = arrayNode.numberNode(currentNonBlockingJsonParser.getDecimalValue());
          return streamArrayNodeOrAddNodeToArray(arrayNode, valueNode);
        }
        break;
      case BIG_INTEGER:
        if (decimalNode instanceof ObjectNode) {
          ((ObjectNode) decimalNode).put(currentFieldName, currentNonBlockingJsonParser.getBigIntegerValue());
        } else {
          ArrayNode arrayNode = (ArrayNode) decimalNode;
          ValueNode valueNode = arrayNode.numberNode(currentNonBlockingJsonParser.getBigIntegerValue());
          return streamArrayNodeOrAddNodeToArray(arrayNode, valueNode);
        }
        break;
      case LONG:
        if (decimalNode instanceof ObjectNode) {
          ((ObjectNode) decimalNode).put(currentFieldName, currentNonBlockingJsonParser.getLongValue());
        } else {
          ArrayNode arrayNode = (ArrayNode) decimalNode;
          ValueNode valueNode = arrayNode.numberNode(currentNonBlockingJsonParser.getLongValue());
          return streamArrayNodeOrAddNodeToArray(arrayNode, valueNode);
        }
        break;
      case INT:
        if (decimalNode instanceof ObjectNode) {
          ((ObjectNode) decimalNode).put(currentFieldName, currentNonBlockingJsonParser.getIntValue());
        } else {
          ArrayNode arrayNode = (ArrayNode) decimalNode;
          ValueNode valueNode = arrayNode.numberNode(currentNonBlockingJsonParser.getIntValue());
          return streamArrayNodeOrAddNodeToArray(arrayNode, valueNode);
        }
        break;
      default:
        // shouldn't get here
        throw new IllegalStateException("Unsupported number type: " + numberType);
      }
      break;
    case VALUE_NULL:
      if (nodeStack.isEmpty()) {
        throw new JsonParseException(currentNonBlockingJsonParser, "Unexpected null literal");
      }
      JsonNode nullNode = nodeStack.peekFirst();
      if (nullNode instanceof ObjectNode) {
        ((ObjectNode) nullNode).putNull(currentFieldName);
      } else {
        ArrayNode arrayNode = (ArrayNode) nullNode;
        ValueNode valueNode = arrayNode.nullNode();
        return streamArrayNodeOrAddNodeToArray(arrayNode, valueNode);
      }
      break;

    case VALUE_TRUE:
    case VALUE_FALSE:
      if (nodeStack.isEmpty()) {
        throw new JsonParseException(currentNonBlockingJsonParser, "Unexpected boolean literal");
      }
      JsonNode booleanNode = nodeStack.peekFirst();
      if (booleanNode instanceof ObjectNode) {
        ((ObjectNode) booleanNode).put(currentFieldName, currentNonBlockingJsonParser.getBooleanValue());
      } else {
        ArrayNode arrayNode = (ArrayNode) booleanNode;
        ValueNode valueNode = arrayNode.booleanNode(currentNonBlockingJsonParser.getBooleanValue());
        return streamArrayNodeOrAddNodeToArray(arrayNode, valueNode);
      }
      break;

    default:
      throw new IllegalStateException("Unsupported JSON event: " + event);
    }

    return null;
  }

  private JsonNode array(JsonNode node) {
    if (node instanceof ObjectNode) {
      return ((ObjectNode) node).putArray(currentFieldName);
    } else if (node instanceof ArrayNode) {
      return ((ArrayNode) node).addArray();
    } else {
      return JsonNodeFactory.instance.arrayNode();
    }
  }

  private JsonNode node(JsonNode node) {
    if (node instanceof ObjectNode) {
      return ((ObjectNode) node).putObject(currentFieldName);
    } else if (node instanceof ArrayNode && !(streamArray && nodeStack.size() == 1)) {
      return ((ArrayNode) node).addObject();
    } else {
      return JsonNodeFactory.instance.objectNode();
    }
  }
}