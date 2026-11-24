package com.ohgiraffer.tukk_tak.tukk_tak;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class TukkTakApplication {

    public static void main(String[] args) {
        SpringApplication.run(TukkTakApplication.class, args);
    }

}
