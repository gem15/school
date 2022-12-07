package com.example.school;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@Slf4j
public class Test {

    @Scheduled(cron="*/5 * * * * *")
    public void logTest(){
        System.out.println("ok");
        for (int i = 0; i < 12; i++) {

            log.info("                                                                                                                             "
            );
        }

    }
}
