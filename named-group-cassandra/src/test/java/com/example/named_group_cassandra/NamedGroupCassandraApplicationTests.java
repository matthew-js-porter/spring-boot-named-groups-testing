package com.example.named_group_cassandra;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import javax.net.ssl.SSLHandshakeException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NamedGroupCassandraApplicationTests {

    @Nested
    @SpringBootTest
    class goodNamedGroupTest {
        @Test
        void contextLoads() {
        }
    }

    @Nested
    @SpringBootTest
    class badNamedGroupTest {
        @Test
        void contextLoads() {
            SpringApplication app = new SpringApplication(NamedGroupCassandraApplication.class);
            assertThatThrownBy(() -> app.run("--tls.named-groups=invalid-group"))
                    .hasStackTraceContaining(SSLHandshakeException.class.getName());
        }
    }
}
