package hello.world;

import javax.inject.Singleton;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;

@Singleton
@Replaces(named = "helloServiceEng", bean = HelloServiceEng.class)
@Requires(env = Environment.TEST) 
public class HelloServiceFr implements HelloService {

    @Override
    public String hello(String name) {
        return "Salut " + name;
    }
}
