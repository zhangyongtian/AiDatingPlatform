package com.dating.platform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.dating.platform.mapper")
@ComponentScan(basePackages = {
    "com.dating.platform.controller",
    "com.dating.platform.service",
    "com.dating.platform.config"
})
public class DatingPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(DatingPlatformApplication.class, args);
    }
} 