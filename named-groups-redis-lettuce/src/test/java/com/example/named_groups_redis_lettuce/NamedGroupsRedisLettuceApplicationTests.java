package com.example.named_groups_redis_lettuce;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class NamedGroupsRedisLettuceApplicationTests {

    @Nested
    class ValidNamedGroupsTest {

        @Test
        void contextLoads() {
        }
    }


    @Nested
    @SpringBootTest(properties = {
            "tls.named-groups=invalid-group"
    })
    class InvalidNamedGroupsTest {

        @Test
        void errorsConnectingToRedis() {
        }
    }

}
