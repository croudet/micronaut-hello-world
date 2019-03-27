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
    HttpRequest request = HttpRequest.POST("/hello/echo/booleans", "[true, true, false]").contentType(MediaType.APPLICATION_JSON_TYPE);
    String body = client.toBlocking().retrieve(request);
    assertNotNull(body);
    System.out.println("echoBooleans " + body);
    assertEquals("[true,true,false]", body);
  }
  
  @Test
  public void echoStrings() {
    HttpRequest request = HttpRequest.POST("/hello/echo/strings", "[\"a\", \"b\", \"c\"]").contentType(MediaType.APPLICATION_JSON_TYPE);
    String body = client.toBlocking().retrieve(request);
    assertNotNull(body);
    System.out.println("echoStrings " + body);
    assertEquals("[\"a\",\"b\",\"c\"]", body);
  }
  
  @Test
  public void echoLongs() {
    HttpRequest request = HttpRequest.POST("/hello/echo/longs", "[1, 10, 100]").contentType(MediaType.APPLICATION_JSON_TYPE);
    String body = client.toBlocking().retrieve(request);
    assertNotNull(body);
    System.out.println("echoLongs " + body);
    assertEquals("[1,10,100]", body);
  }
  
  @Test
  public void echoListLongs() {
    HttpRequest request = HttpRequest.POST("/hello/echo/listlongs", "[1, 10, 100]").contentType(MediaType.APPLICATION_JSON_TYPE);
    String body = client.toBlocking().retrieve(request);
    assertNotNull(body);
    System.out.println("echoListLongs " + body);
    assertEquals("[1,10,100]", body);
  }
  
  @Test
  public void echoListStrings() {
    HttpRequest request = HttpRequest.POST("/hello/echo/liststrings", "[\"a\", \"b\", \"c\"]").contentType(MediaType.APPLICATION_JSON_TYPE);
    String body = client.toBlocking().retrieve(request);
    assertNotNull(body);
    System.out.println("echoListStrings " + body);
    assertEquals("[\"a\",\"b\",\"c\"]", body);
  }
  
  @Test
  public void echoListBooleans() {
    HttpRequest request = HttpRequest.POST("/hello/echo/listbooleans", "[true, true, false]").contentType(MediaType.APPLICATION_JSON_TYPE);
    String body = client.toBlocking().retrieve(request);
    assertNotNull(body);
    System.out.println("echoListBooleans " + body);
    assertEquals("[true,true,false]", body);
  }
}
