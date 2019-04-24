package hello.world;

import java.util.List;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Consumes;
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

    @Patch(uri = "/dummy/patch", consumes = MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    Dummy patch(@Body Flowable<Long> ids);

    @Patch(uri = "/dummy/patch/list", consumes = MediaType.APPLICATION_JSON)
    Dummy patchList(@Body List<Long> ids);

    @Patch("/dummy/patch/obj")
    Dummy patchDummies(@Body Flux<Dummy> dummies);
}
