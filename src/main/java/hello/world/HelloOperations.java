package hello.world;

import javax.annotation.Nullable;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import io.micronaut.http.annotation.Get;
import io.reactivex.Flowable;
import reactor.core.publisher.Flux;

public interface HelloOperations {

    /**
     * Lists the channel updates stored in the database.
     *
     * @param offset The offset.
     * @param max    The maximum number of returned objects.
     * @param sort   The sort order.
     * @return A list of ChannelUpdates.
     */
    @Get("/channel-updates{?offset,max,sort:[asc|desc|ASC|DESC]}")
    Flux<String> listChannelUpdates(@Nullable @Min(0L) Integer offset, @Nullable @Min(1L) @Max(10000L) Integer max,
            @Nullable SortOrder sort);

    /**
     * Lists the assets stored in the database.
     *
     * @param offset The offset.
     * @param max    The maximum number of returned objects.
     * @param sort   The sort order.
     * @return A list of Assets.
     */
    @Get("/assets{?offset,max,sort:[asc|desc|ASC|DESC]}")
    Flux<String> listAssets(@Nullable @Min(0L) Integer offset, @Nullable @Min(1L) @Max(10000L) Integer max,
            @Nullable SortOrder sort);

    /**
     * Lists the assets stored in the database.
     *
     * @param offset The offset.
     * @param max    The maximum number of returned objects.
     * @param sort   The sort order.
     * @return A list of Assets.
     */
    @Get("/rx/assets{?offset,max,sort:[asc|desc|ASC|DESC]}")
    Flowable<String> listAssetsRx(@Nullable @Min(0L) Integer offset, @Nullable @Min(1L) @Max(10000L) Integer max,
            @Nullable SortOrder sort);
}
