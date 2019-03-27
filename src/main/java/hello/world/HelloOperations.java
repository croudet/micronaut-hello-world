package hello.world;

import java.util.List;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Post;
import reactor.core.publisher.Flux;

public interface HelloOperations {

  @Post("/echo/booleans")
  Flux<Boolean> echoBooleans(@Body Flux<Boolean> bools);
  
  @Post("/echo/strings")
  Flux<String> echoStrings(@Body Flux<String> strings);
  
  @Post("/echo/longs")
  Flux<Long> echoLongs(@Body Flux<Long> longs);
  
  @Post("/echo/listlongs")
  Flux<Long> echoListLongs(@Body List<Long> longs);
  
}
