package hello.world;

import javax.inject.Singleton;

import io.micronaut.scheduling.annotation.Scheduled;

@Singleton
public class HelloService {

    @Scheduled(cron = "0 15 10 ? * MON")
    void everyMondayAtTenFifteenAm() {
        System.out.println("Executing everyMondayAtTenFifteenAm()");
    }
}
