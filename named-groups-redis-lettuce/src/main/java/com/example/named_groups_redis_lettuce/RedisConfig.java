package com.example.named_groups_redis_lettuce;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.SslOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.data.redis.autoconfigure.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.security.NoSuchAlgorithmException;

@Configuration
public class RedisConfig {

    @Bean
    LettuceClientConfigurationBuilderCustomizer namedGroupCustomizer(@Value("${tls.named-groups:}") String[] namedGroups) throws NoSuchAlgorithmException {
        final SSLParameters sslParameters = SSLContext.getDefault().getDefaultSSLParameters();
        sslParameters.setNamedGroups(namedGroups);
        return builder -> builder.clientOptions(ClientOptions.builder()
                .sslOptions(SslOptions.builder().sslParameters(() -> sslParameters).build()).build());
    }
}
