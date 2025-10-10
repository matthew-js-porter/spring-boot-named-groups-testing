package com.example.named_group_cassandra;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

class NamedGroupCassandraApplicationTests {

    @Nested
    @SpringBootTest
    class goodNamedGroupTest {
        @Test
        void contextLoads() {
        }
    }

    @Nested
    @SpringBootTest(properties = "named-group=BAD")
    class badNamedGroupTest {
        @Test
        void contextLoads() {
        }
    }
}
