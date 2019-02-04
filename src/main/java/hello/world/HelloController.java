package hello.world;

import javax.inject.Inject;
import javax.inject.Named;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;

@Controller("/hello") 
public class HelloController {
    @Inject @Named("helloServiceEng") HelloService helloService;
    
    @Get(uri = "/{name}", produces = MediaType.TEXT_PLAIN) 
    public String hello(String name) {
        return helloService.hello(name); 
    }
}