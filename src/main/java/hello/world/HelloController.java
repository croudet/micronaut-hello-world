package hello.world;

import io.micronaut.http.annotation.Controller;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

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
    @ApiResponse(responseCode = "200", description = "Returns a dummy", content = @Content(schema = @Schema(implementation = Dummy.class)))
    @ApiResponse(responseCode = "500", description = "Internal Error")
    public Dummy dummyAnnot() {
        return new Dummy();
    }

    @Override
    @Operation(description = "Returns A dummy", summary = "Returns a Dummy")
    @ApiResponse(responseCode = "200", description = "Returns a dummy", content = @Content(schema = @Schema(implementation = Dummy.class)))
    @ApiResponse(responseCode = "500", description = "Internal Error")
    public Single<Dummy> dummySingleAnnot() {
        return Single.just(new Dummy());
    }

    @Override
    @Operation(description = "Returns A dummy", summary = "Returns a Dummy")
    @ApiResponse(responseCode = "200", description = "Returns a dummy", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Dummy.class))))
    @ApiResponse(responseCode = "500", description = "Internal Error")
    public Flowable<Dummy> dummAnnotFlowable() {
        return Flowable.just(new Dummy());
    }

}
