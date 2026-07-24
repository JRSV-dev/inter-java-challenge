package com.inter.java.challenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class InterJavaChallengeApplication {

    public static void main(String[] args) {
        SpringApplication.run(InterJavaChallengeApplication.class, args);
    }

}
