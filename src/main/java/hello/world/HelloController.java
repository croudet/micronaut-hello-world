package hello.world;

import java.util.List;

import io.micronaut.http.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Controller("/hello") 
public class HelloController implements HelloOperations {

  @Override
  public Flux<Boolean> echoBooleans(Flux<Boolean> bools) {
    return bools.subscribeOn(Schedulers.elastic()).map(b -> b);
  }

  @Override
  public Flux<String> echoStrings(Flux<String> strings) {
    return strings.subscribeOn(Schedulers.elastic()).map(b -> b);
  }

  @Override
  public Flux<Long> echoLongs(Flux<Long> longs) {
    return longs.subscribeOn(Schedulers.elastic()).map(b -> b);
  }

  @Override
  public Flux<Long> echoListLongs(List<Long> longs) {
    return Flux.fromIterable(longs);
  }

  @Override
  public Flux<Boolean> echoListBooleans(List<Boolean> bools) {
    return Flux.fromIterable(bools);
  }

  @Override
  public Flux<String> echoListStrings(List<String> strings) {
    return Flux.fromIterable(strings);
  }
    
}
