package com.example.named_groups_redis_lettuce;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.SpringApplication;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.RedisTemplate;

import javax.net.ssl.SSLHandshakeException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

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
    class InvalidNamedGroupsTest {

        @Test
        void applicationFailsToStart() {
            SpringApplication app = new SpringApplication(NamedGroupsRedisLettuceApplication.class);
            var context = app.run("--tls.named-groups=invalid-group");
            RedisTemplate<String, String> redisTemplate = context.getBeanProvider(new ParameterizedTypeReference<@NotNull RedisTemplate<String, String> >() {}).getObject();
            assertThatThrownBy(() -> redisTemplate.opsForValue().set("test", "value"))
                    .hasRootCauseExactlyInstanceOf(SSLHandshakeException.class);
        }
    }

}
