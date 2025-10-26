package com.example.named_group_cassandra;

import com.datastax.oss.driver.api.core.metadata.EndPoint;
import com.datastax.oss.driver.api.core.ssl.ProgrammaticSslEngineFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;

@SpringBootApplication
public class NamedGroupCassandraApplication {

	public static void main(String[] args) {
		SpringApplication.run(NamedGroupCassandraApplication.class, args);
	}

    @Bean
    CqlSessionBuilderCustomizer sslCustomizer(@Value("${tls.named-groups}") String[] namedGroups, final SslBundles sslBundles) {
        final SSLContext sslContext = sslBundles.getBundle("cassandra-ssl").createSslContext();
        return cqlSessionBuilder -> cqlSessionBuilder.withSslEngineFactory(new ProgrammaticSslEngineFactory(sslContext, new String[] {"TLS_AES_128_GCM_SHA256"}) {
            @Override
            @NonNull
            public SSLEngine newSslEngine(@NonNull EndPoint remoteEndpoint) {
                final var sslEngine = super.newSslEngine(remoteEndpoint);
                final SSLParameters sslParameters = sslEngine.getSSLParameters();
                sslParameters.setNamedGroups(namedGroups);
                sslEngine.setSSLParameters(sslParameters);
                return sslEngine;
            }
        });
    }
}
