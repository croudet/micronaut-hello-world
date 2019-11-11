package hello.world.controller;

import java.util.List;

import hello.world.Asset;
import hello.world.Dummy;
import hello.world.HelloOperations;
import hello.world.SortOrder;
import io.micronaut.http.annotation.Controller;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import reactor.core.publisher.Flux;

@Controller("/hello")
public class HelloController implements HelloOperations {

    @Override
    public Dummy dummy() {
        return new Dummy();
    }

    @Override
    public Single<Dummy> dummySingle() {
        return Single.just(new Dummy());
    }

    @Override
    public Flowable<Dummy> dummyFlowable() {
        return Flowable.just(new Dummy());
    }

    @Override
    @Operation(description = "Returns A dummy", summary = "Returns a Dummy")
    @ApiResponse(responseCode = "200", description = "Returns a dummy", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Dummy.class)))
    @ApiResponse(responseCode = "500", description = "Internal Error")
    public Dummy dummyAnnot() {
        return new Dummy();
    }

    @Override
    @Operation(description = "Returns A dummy", summary = "Returns a Dummy")
    @ApiResponse(responseCode = "200", description = "Returns a dummy", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Dummy.class)))
    @ApiResponse(responseCode = "500", description = "Internal Error")
    public Single<Dummy> dummySingleAnnot() {
        return Single.just(new Dummy());
    }

    @Override
    @Operation(description = "Returns A dummy", summary = "Returns a Dummy")
    @ApiResponse(responseCode = "200", description = "Returns a dummy", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Dummy.class))))
    @ApiResponse(responseCode = "500", description = "Internal Error")
    public Flowable<Dummy> dummAnnotFlowable() {
        return Flowable.just(new Dummy());
    }

    @Override
    public Dummy patch(Flowable<Long> ids) {
        System.out.println("COUNT " + ids.count().blockingGet());
        return new Dummy();
    }

    @Override
    public Dummy patchList(List<Long> ids) {
        System.out.println("COUNT " + ids.size());
        return new Dummy();
    }

    @Override
    public Dummy patchDummies(Flux<Dummy> ids) {
        System.out.println("COUNT " + ids.count().block());
        return new Dummy();
    }

    @Override
    public Flux<Asset> listAssets(String publisher, Integer offset, Integer max, SortOrder sort) {
        return Flux.empty();
    }

}
