package com.wdy.module;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(scanBasePackages = {"com.wdy.module"})
@EnableCaching
public class EslsWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(EslsWebApplication.class, args);
    }

}
