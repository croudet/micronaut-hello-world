package hello.world;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "SortOrder")
public enum SortOrder {
    ASC, DESC;
}
