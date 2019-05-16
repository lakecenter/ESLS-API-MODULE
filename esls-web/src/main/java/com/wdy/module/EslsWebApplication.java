package com.wdy.module;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = {"com.wdy.module"})
@EnableCaching
@EnableAsync
public class EslsWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(EslsWebApplication.class, args);
    }

}
