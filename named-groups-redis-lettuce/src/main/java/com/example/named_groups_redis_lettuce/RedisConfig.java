package com.example.named_groups_redis_lettuce;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.SslOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.security.NoSuchAlgorithmException;

@Configuration
public class RedisConfig {

    @Bean
    public LettuceConnectionFactory redisConnectionFactory(@Value("${tls.named-groups}")final String[] namedGroups) throws NoSuchAlgorithmException {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName("localhost");
        config.setPort(6380);

        final SSLParameters sslParameters = SSLContext.getDefault().getDefaultSSLParameters();
        sslParameters.setNamedGroups(namedGroups);
        SslOptions sslOptions = SslOptions.builder()
                .sslParameters(() -> sslParameters)
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
