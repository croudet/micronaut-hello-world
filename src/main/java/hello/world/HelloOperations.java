package hello.world;

import java.util.List;

import javax.annotation.Nullable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.validation.Validated;
import io.reactivex.Flowable;
import io.reactivex.Single;
import reactor.core.publisher.Flux;

@Validated
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

    @Post("/dummy/post/obj")
    Dummy postDummy(@Body @Nullable Dummy dummy);

    @Get("/publisher/{publisher}/assets{?offset,max,sort:[asc|desc|ASC|DESC]}")
    Flux<Asset> listAssets(@PathVariable @NotBlank String publisher, @QueryValue @Nullable @Min(0L) Integer offset, @QueryValue @Nullable @Min(1L) Integer max, @QueryValue @Nullable SortOrder sort);
}
