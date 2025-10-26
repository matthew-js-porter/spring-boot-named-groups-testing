package com.example.named_groups_redis_lettuce;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.File;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    @Bean
    @ServiceConnection(name = "redis")
    ComposeContainer redisContainer() {
        return new ComposeContainer(DockerImageName.parse("docker:24.0.2"), new File("compose.yaml"));
    }

}