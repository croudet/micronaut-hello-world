package hello.world;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import io.micronaut.context.ApplicationContext;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.runtime.server.EmbeddedServer;

public class HelloWorldControllerTest {
    private static EmbeddedServer server; 
    private static HttpClient client;

@BeforeClass 
public static void setupServer() {
    server = ApplicationContext.run(EmbeddedServer.class); 
    client = server.getApplicationContext() .createBean(HttpClient.class, server.getURL());
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
 public void helloFr() { 
     HttpRequest request = HttpRequest.GET("/hello/John"); 
     String body = client.toBlocking().retrieve(request);
     assertNotNull(body);
     assertEquals(
             "Salut John",
             body
     );
 } 
}
