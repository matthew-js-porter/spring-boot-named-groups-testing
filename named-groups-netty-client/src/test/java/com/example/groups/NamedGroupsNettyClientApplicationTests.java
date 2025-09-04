package com.example.groups;

import io.netty.handler.ssl.SslContextBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.ssl.SslManagerBundle;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.util.function.ThrowingConsumer;

@SpringBootTest(
		properties = {
				"spring.ssl.bundle.jks.client.truststore.location=classpath:keystore/keystore.p12",
				"spring.ssl.bundle.jks.client.truststore.password=password",
		},
		webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
class NamedGroupsNettyClientApplicationTests {

	@Autowired
	SslBundles sslBundles;


	@Test
	void validNameGroup() {
		final SslBundle sslBundle  = sslBundles.getBundle("client");
		final RestTestClient restTestClient = RestTestClient.bindToServer(netty(sslBundle, "x25519"))
				.baseUrl("https://localhost:8443")
				.build();
		restTestClient.get().uri("/actuator/info").exchange().expectStatus().is2xxSuccessful();
	}

	private ClientHttpRequestFactory netty(final SslBundle sslBundle, final String nameGroup) {
		return ClientHttpRequestFactoryBuilder.reactor().withHttpClientCustomizer(httpClient -> httpClient.secure((ThrowingConsumer.of((spec)  -> {
            SslManagerBundle managers = sslBundle.getManagers();
            SslContextBuilder builder = SslContextBuilder.forClient()
                    .keyManager(managers.getKeyManagerFactory())
                    .trustManager(managers.getTrustManagerFactory());
            spec.sslContext(builder.build());
        })))).build();
	}
}
