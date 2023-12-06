package com.opinionowl.opinionowl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * The starting point for the application.
 */
@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class OpinionOwlApplication {
    public static void main(String[] args) {
        SpringApplication.run(OpinionOwlApplication.class, args);
    }

}
