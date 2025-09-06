package com.example.named.groups;

import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy;
import org.apache.hc.client5.http.ssl.DefaultHostnameVerifier;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.ssl.SslOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.test.web.servlet.client.RestTestClient;

import javax.net.ssl.*;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(
		properties = {
				"spring.ssl.bundle.jks.client.truststore.location=classpath:keystore/keystore.p12",
				"spring.ssl.bundle.jks.client.truststore.password=password",
		},
		webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
class NamedGroupsHttpComponentsClientApplicationTests {

	@Autowired
	SslBundles sslBundles;

	@Test
	void validNameGroup() {
		final SslBundle sslBundle  = sslBundles.getBundle("client");
		final RestTestClient restTestClient = RestTestClient.bindToServer(httpComponents(sslBundle, "secp256r1"))
				.baseUrl("https://localhost:8443")
				.build();
		restTestClient.get().uri("/actuator/info").exchange().expectStatus().is2xxSuccessful();
	}

	@Test
	void invalidNameGroup() {
		final SslBundle sslBundle  = sslBundles.getBundle("client");
		final RestTestClient restTestClient = RestTestClient.bindToServer(httpComponents(sslBundle, "BAD"))
				.baseUrl("https://localhost:8443")
				.build();
		assertThatThrownBy(() -> restTestClient.get().uri("/actuator/info").exchange()).hasCauseInstanceOf(SSLHandshakeException.class);
	}

	private ClientHttpRequestFactory httpComponents(final SslBundle sslBundle, final String nameGroup) {
		return ClientHttpRequestFactoryBuilder.httpComponents()
				.withTlsSocketStrategyFactory(ssl -> {
					if (ssl == null) {
						return null;
					} else {
						SslOptions options = ssl.getOptions();
						SSLContext sslContext = ssl.createSslContext();
						return new DefaultClientTlsStrategy(sslContext, options.getEnabledProtocols(), options.getCiphers(), null, new DefaultHostnameVerifier()){
							@Override
							protected void initializeSocket(SSLSocket socket) {
								SSLParameters sslParameters = socket.getSSLParameters();
								sslParameters.setNamedGroups(new String[]{nameGroup});
								socket.setSSLParameters(sslParameters);
								super.initializeSocket(socket);
							}
						};
					}
		}).build(ClientHttpRequestFactorySettings.ofSslBundle(sslBundle));
	}
}
