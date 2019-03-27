package hello.world;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import io.micronaut.context.ApplicationContext;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.runtime.server.EmbeddedServer;
import reactor.core.publisher.Flux;

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
  public void echoBools() {
    HttpRequest request = HttpRequest.POST("/hello/echo/booleans", Arrays.asList(Boolean.TRUE, Boolean.TRUE, Boolean.FALSE)).contentType(MediaType.APPLICATION_JSON_TYPE);
    String body = client.toBlocking().retrieve(request);
    assertNotNull(body);
    System.out.println("echoBooleans " + body);
    assertEquals("[true,true,false]", body);
  }
  
  @Test
  public void echoStrings() {
    HttpRequest request = HttpRequest.POST("/hello/echo/strings", Arrays.asList("a", "b", "c")).contentType(MediaType.APPLICATION_JSON_TYPE);
    String body = client.toBlocking().retrieve(request);
    assertNotNull(body);
    System.out.println("echoStrings " + body);
    assertEquals("[\"a\",\"b\",\"c\"]", body);
  }
  
  @Test
  public void echoLongs() {
    HttpRequest request = HttpRequest.POST("/hello/echo/longs", Arrays.asList(1L, 10L, 100L)).contentType(MediaType.APPLICATION_JSON_TYPE);
    String body = client.toBlocking().retrieve(request);
    assertNotNull(body);
    System.out.println("echoLongs " + body);
    assertEquals("[1,10,100]", body);
  }
  
  @Test
  public void echoListLongs() {
    HttpRequest request = HttpRequest.POST("/hello/echo/listlongs", Arrays.asList(1L, 10L, 100L)).contentType(MediaType.APPLICATION_JSON_TYPE);
    String body = client.toBlocking().retrieve(request);
    assertNotNull(body);
    System.out.println("echoListLongs " + body);
    assertEquals("[1,10,100]", body);
  }
}
