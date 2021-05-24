package org.ls.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CoreAutoConfiguration {
    public static void main(String[] args) {
        SpringApplication.run(CoreAutoConfiguration.class, "--debug");
    }
}
