package hello.world;

import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import io.micronaut.context.ApplicationContext;
import io.micronaut.runtime.server.EmbeddedServer;
import io.reactivex.Flowable;

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

//    @Test
//    public void testPrimitiveBodyList() {
//        List<Long> l = Arrays.asList(11L, 12L, 13L);
//        assertNotNull(helloClient.patchList(l));
//    }

    @Test
    public void testPrimitiveBodyFlux() {
        List<Long> l = Arrays.asList(11L, 12L, 13L);
        assertNotNull(helloClient.patch(Flowable.fromIterable(l)));
    }

//    @Test
//    public void testObjectBodyFlux() {
//        List<Dummy> l = Arrays.asList(new Dummy(), new Dummy(), new Dummy());
//        assertNotNull(helloClient.patchDummies(Flux.fromIterable(l)));
//    }

    @Test
    public void testPageable() {
        helloClient.listAssets("pub", null, null, null).blockFirst();
        helloClient.listAssets("pub", null, null, SortOrder.ASC).blockFirst();
        helloClient.listAssets("pub", 1, null, SortOrder.DESC).blockFirst();
        helloClient.listAssets("pub", null, 10, SortOrder.DESC).blockFirst();
        helloClient.listAssets("pub", 1, 10, SortOrder.DESC).blockFirst();
        helloClient.listAssets("pub", 1, 10, null).blockFirst();
    }
}
