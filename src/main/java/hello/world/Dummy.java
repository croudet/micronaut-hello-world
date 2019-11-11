package hello.world;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Dummy", description = "A dummy class.")
public class Dummy {
    @Nullable
    long id;

    @Nullable
    private String foo = "foo";

    private String bar = "bar";

    @Schema(name = "foo", description = "A foo field.")
    public String getFoo() {
        return foo;
    }

    public void setFoo(String foo) {
        this.foo = foo;
    }

    @Schema(name = "bar", description = "A bar field.")
    public String getBar() {
        return bar;
    }

    public void setBar(String bar) {
        this.bar = bar;
    }

}