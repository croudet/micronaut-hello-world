package hello.world;

import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import io.micronaut.context.ApplicationContext;
import io.micronaut.runtime.server.EmbeddedServer;
import reactor.core.publisher.Flux;

public class HelloWorldControllerTest {
    private static EmbeddedServer server;
    private static HelloClient helloClient;

    @BeforeClass
    public static void setupServer() {
        server = ApplicationContext.run(EmbeddedServer.class);
        helloClient = server.getApplicationContext().getBean(HelloClient.class);
    }

    @AfterClass
    public static void stopServer() {
        if (server != null) {
            server.stop();
        }
    }

    @Test
    public void testPrimitiveBodyFlux() {
        List<Long> l = Arrays.asList(1L, 2L, 3L);
        assertNotNull(helloClient.patch(Flux.fromIterable(l)));
    }

    @Test
    public void testObjectBodyFlux() {
        List<Dummy> l = Arrays.asList(new Dummy(), new Dummy(), new Dummy());
        assertNotNull(helloClient.patchDummies(Flux.fromIterable(l)));
    }
}
