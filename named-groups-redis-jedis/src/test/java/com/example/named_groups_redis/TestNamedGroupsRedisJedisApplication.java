package com.example.named_groups_redis;

import org.springframework.boot.SpringApplication;
import org.testcontainers.utility.TestcontainersConfiguration;

public class TestNamedGroupsRedisJedisApplication {

    public static void main(String[] args) {
        SpringApplication.from(NamedGroupsRedisJedisApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}