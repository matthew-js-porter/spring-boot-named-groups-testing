package com.example.named_group_kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//@SpringBootTest(properties = "spring.kafka.producer.properties.ssl.groups=BAD")
@SpringBootTest
@Testcontainers
@DirtiesContext
public class KafkaSslIntegrationTest {

    @Container
    static ComposeContainer compose = new ComposeContainer(new File("compose.yml"))
            .withExposedService("kafka", 9093)
            .withExposedService("zookeeper", 2181);

    @Autowired
    private KafkaTemplate<@NotNull String, @NotNull String> kafkaTemplate;

    private final CountDownLatch latch = new CountDownLatch(1);
    private String receivedMessage;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers",
            () -> "localhost:" + compose.getServicePort("kafka", 9093));
    }

    @Test
    public void testPublishToKafkaOverSsl() throws InterruptedException {
        String topic = "test-topic";
        String message = "Hello Kafka SSL with Testcontainers!";

        // Send message to Kafka
        kafkaTemplate.send(topic, message);

        // Wait for message to be received
        boolean messageReceived = latch.await(30, TimeUnit.SECONDS);
        
        assertTrue(messageReceived, "Message should be received within 30 seconds");
        assertEquals(message, receivedMessage, "Received message should match sent message");
    }

    @KafkaListener(topics = "test-topic", groupId = "test-group")
    public void listen(ConsumerRecord<String, String> record) {
        receivedMessage = record.value();
        latch.countDown();
    }
}
