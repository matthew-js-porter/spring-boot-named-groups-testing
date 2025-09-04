package com.example.named_groups_jdk_client;

import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.test.web.servlet.client.RestTestClient;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLParameters;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(
		properties = {
				"spring.ssl.bundle.jks.client.truststore.location=classpath:keystore/keystore.p12",
				"spring.ssl.bundle.jks.client.truststore.password=password",
		},
		webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
class NamedGroupsTomcatServerApplicationTests {

	@Autowired
	SslBundles sslBundles;


	@Test
	void validNameGroup() {
		final SslBundle sslBundle  = sslBundles.getBundle("client");
		final RestTestClient restTestClient = RestTestClient.bindToServer(jetty(sslBundle, "secp256r1"))
				.baseUrl("https://localhost:8443")
				.build();
		restTestClient.get().uri("/actuator/info").exchange().expectStatus().is2xxSuccessful();
	}

	@Test
	void invalidNameGroup() {
		final SslBundle sslBundle  = sslBundles.getBundle("client");
		final RestTestClient restTestClient = RestTestClient.bindToServer(jetty(sslBundle, "x25519"))
				.baseUrl("https://localhost:8443")
				.build();
		assertThatThrownBy(() -> restTestClient.get().uri("/actuator/info").exchange()).hasCauseInstanceOf(SSLHandshakeException.class);
	}

	private ClientHttpRequestFactory jetty(final SslBundle sslBundle, final String nameGroup) {
		return ClientHttpRequestFactoryBuilder.jetty().withClientConnectorCustomizerCustomizer(clientConnector -> {
			SSLContext sslContext = sslBundle.createSslContext();
			SslContextFactory.Client factory = new SslContextFactory.Client() {
				@Override
				public SSLParameters customize(SSLParameters sslParams) {
					sslParams.setNamedGroups(new String[]{nameGroup});
					return super.customize(sslParams);
				}
			};
			factory.setSslContext(sslContext);
			clientConnector.setSslContextFactory(factory);
		}).build();
	}
}
