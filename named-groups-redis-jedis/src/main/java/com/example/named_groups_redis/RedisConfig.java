package com.example.named_groups_redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.data.redis.autoconfigure.JedisClientConfigurationBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.security.NoSuchAlgorithmException;

@Configuration
public class RedisConfig {

    @Bean
    JedisClientConfigurationBuilderCustomizer namedGroupCustomizer(@Value("${tls.named-groups:}") String[] namedGroups) throws NoSuchAlgorithmException {
        final SSLParameters sslParameters = SSLContext.getDefault().getDefaultSSLParameters();
        sslParameters.setNamedGroups(namedGroups);
        return builder -> builder.customize(customizer -> customizer.sslParameters(sslParameters));
    }
}
