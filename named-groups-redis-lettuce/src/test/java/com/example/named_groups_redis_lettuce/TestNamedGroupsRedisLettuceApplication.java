package com.example.named_groups_redis_lettuce;

import org.springframework.boot.SpringApplication;
import org.testcontainers.utility.TestcontainersConfiguration;

public class TestNamedGroupsRedisLettuceApplication {

    public static void main(String[] args) {
        SpringApplication.from(NamedGroupsRedisLettuceApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}