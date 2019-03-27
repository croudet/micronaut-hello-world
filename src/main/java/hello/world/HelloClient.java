package hello.world;

import io.micronaut.http.client.annotation.Client;

@Client("/hello")
public interface HelloClient extends HelloOperations {

}
