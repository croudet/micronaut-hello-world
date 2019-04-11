package hello.world;

import io.micronaut.http.annotation.Get;
import io.reactivex.Flowable;
import io.reactivex.Single;

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
}
