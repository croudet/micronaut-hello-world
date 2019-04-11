package hello.world;

import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;

import io.micronaut.context.ApplicationContext;
import io.micronaut.http.client.HttpClient;
import io.micronaut.jackson.parser.JacksonProcessor;
import io.micronaut.runtime.server.EmbeddedServer;

public class HelloWorldControllerTest {
    private static EmbeddedServer server;
    private static HttpClient client;

    @BeforeClass
    public static void setupServer() {
        server = ApplicationContext.run(EmbeddedServer.class);
        client = server.getApplicationContext().createBean(HttpClient.class, server.getURL());
    }

    @AfterClass
    public static void stopServer() {
        if (server != null) {
            server.stop();
        }
        if (client != null) {
            client.stop();
        }
    }

    @Test
    public void jacksonProcessorArrayStream() {
        // String[] arrays = new String[] {"[1, 2, [3, 4, [5, 6], 7], [8, 9, 10], 11,
        // 12]"};
        String[] arrays = new String[] { "[1]", "[1,2,3]", "[null,1,2,3]", "[null,null,null]", "[2.45,3.45,4.45]",
                "[true,false,true]", "[\"a\"]", "[\"a\", \"b\", \"c\"]", "[]", "[{\"a\": 123}]",
                "[{\"a\": 123}, {\"b\": 456}]", "[{\"a\": [7, 8, 9]}, {\"b\": {\"c\": [9, 9]}}]",
                "[1, 2, [3, 4, [5, 6], 7], [8, 9, 10], 11, 12]" };
        for (String arr : arrays) {
            JacksonProcessor processor = new JacksonProcessor(new JsonFactory(), true);
            processor.subscribe(new Subscriber<JsonNode>() {

                @Override
                public void onSubscribe(Subscription s) {
                    System.out.println("On Subscribe");
                    s.request(Long.MAX_VALUE);
                }

                @Override
                public void onNext(JsonNode t) {
                    System.out.print("OnNext " + t.getNodeType() + " " + t + " - ");
                }

                @Override
                public void onError(Throwable t) {
                    System.out.println("Error " + t);
                }

                @Override
                public void onComplete() {
                    System.out.println("\nComplete");
                }
            });
            processor.onSubscribe(new Subscription() {
                @Override
                public void request(long n) {

                }

                @Override
                public void cancel() {

                }
            });

            processor.onNext(arr.getBytes(StandardCharsets.UTF_8));
            processor.onComplete();
        }
    }

    static class ProcessorSubscriber implements Subscriber<JsonNode> {
        int nodeCount = 0;

        @Override
        public void onSubscribe(Subscription s) {
            s.request(Long.MAX_VALUE);
        }

        @Override
        public void onNext(JsonNode t) {
            ++nodeCount;
        }

        @Override
        public void onError(Throwable t) {
        }

        @Override
        public void onComplete() {
        }
    }

    @Test
    public void jacksonProcessorNestedArrayStream() {
        String arr = "[1, 2, [3, 4, [5, 6], 7], [8, 9, 10], 11, 12]";
        ProcessorSubscriber subscriber = new ProcessorSubscriber();
        JacksonProcessor processor = new JacksonProcessor(new JsonFactory(), true);
        processor.subscribe(subscriber);
        processor.onSubscribe(new Subscription() {
            @Override
            public void request(long n) {

            }

            @Override
            public void cancel() {

            }
        });

        processor.onNext(arr.getBytes(StandardCharsets.UTF_8));
        processor.onComplete();
        assertEquals(6, subscriber.nodeCount);
    }

    @Test
    public void listDummies() {
        System.out.println(client.toBlocking().exchange("/hello/dummies").code());
        System.out.println(client.toBlocking().exchange("/hello/dummies").getContentLength());
        System.out.println(client.toBlocking().exchange("/hello/dummies").toString());
    }
}
