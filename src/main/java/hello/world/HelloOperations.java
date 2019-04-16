package hello.world;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Patch;
import io.reactivex.Flowable;
import io.reactivex.Single;
import reactor.core.publisher.Flux;

public interface HelloOperations {

    @Get("/dummy")
    Dummy dummy();

    @Get("/dummyMono")
    Single<Dummy> dummySingle();

    @Get("/dummyFlowable")
    Flowable<Dummy> dummyFlowable();

    @Get("/dummyAnnot")
    Dummy dummyAnnot();

    @Get("/dummySingleAnnot")
    Single<Dummy> dummySingleAnnot();

    @Get("/dummyFlowableAnnot")
    Flowable<Dummy> dummAnnotFlowable();

    @Patch("/dummy/patch")
    Dummy patch(@Body Flux<Long> ids);
}
