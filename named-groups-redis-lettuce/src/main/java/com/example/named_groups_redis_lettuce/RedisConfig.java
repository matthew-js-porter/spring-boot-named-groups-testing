package com.example.named_groups_redis_lettuce;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.SslOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
public class RedisConfig {

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName("localhost");
        config.setPort(6380);

        SslOptions sslOptions = SslOptions.builder()
                .jdkSslProvider()
                .build();

        ClientOptions clientOptions = ClientOptions.builder()
                .sslOptions(sslOptions)
                .build();

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .useSsl()
                .disablePeerVerification()
                .and()
                .clientOptions(clientOptions)
                .build();

        return new LettuceConnectionFactory(config, clientConfig);
    }
}
