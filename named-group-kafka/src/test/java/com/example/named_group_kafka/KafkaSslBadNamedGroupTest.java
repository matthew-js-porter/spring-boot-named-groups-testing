package com.example.named_group_kafka;

import org.apache.kafka.common.errors.SslAuthenticationException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest(properties = "spring.kafka.producer.properties.ssl.groups=BAD")
@Testcontainers
@DirtiesContext
public class KafkaSslBadNamedGroupTest {

    @Container
    static ComposeContainer compose = new ComposeContainer(new File("compose.yml"))
            .withExposedService("kafka", 9093)
            .withExposedService("zookeeper", 2181);

    @Autowired
    private KafkaTemplate<@NotNull String, @NotNull String> kafkaTemplate;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers",
                () -> "localhost:" + compose.getServicePort("kafka", 9093));
    }

    @Test
    public void testPublishToKafkaOverSsl() {
        String topic = "test-topic";
        String message = "Hello Kafka SSL with Testcontainers!";
        assertThatThrownBy(() -> kafkaTemplate.send(topic, message)).hasCauseInstanceOf(SslAuthenticationException.class);
    }
}
