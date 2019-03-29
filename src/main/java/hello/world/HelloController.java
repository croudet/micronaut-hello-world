package hello.world;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import io.micronaut.http.annotation.Controller;
import io.reactivex.Flowable;
import reactor.core.publisher.Flux;

@Controller("/hello")
public class HelloController implements HelloOperations {

    @Override
    public Flowable<String> listAssetsRx(@Min(0) Integer offset, @Min(1) @Max(10000) Integer max, SortOrder sort) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Flux<String> listAssets(@Min(0) Integer offset, @Min(1) @Max(10000) Integer max, SortOrder sort) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Flux<String> listChannelUpdates(@Min(0) Integer offset, @Min(1) @Max(10000) Integer max, SortOrder sort) {
        // TODO Auto-generated method stub
        return null;
    }

}
