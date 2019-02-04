package hello.world;

public interface HelloService {
    
    default String hello(String name) {
        return "Hello " +  name;
    }
}
