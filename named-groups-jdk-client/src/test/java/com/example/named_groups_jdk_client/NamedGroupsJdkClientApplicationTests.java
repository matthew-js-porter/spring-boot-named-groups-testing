package com.example.named_groups_jdk_client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.test.web.servlet.client.RestTestClient;

import javax.net.ssl.SSLParameters;
import java.net.http.HttpClient;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(
		properties = {
				"spring.ssl.bundle.jks.client.truststore.location=classpath:keystore/keystore.p12",
				"spring.ssl.bundle.jks.client.truststore.password=password",
		},
		webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
class NamedGroupsJdkClientApplicationTests {

	@Autowired
	SslBundles sslBundles;


	@Test
	void validNameGroup() {
		final SslBundle sslBundle  = sslBundles.getBundle("client");
		final HttpClient httpClient = buildWithNamedGroup("x25519", sslBundle);
		final RestTestClient restTestClient = RestTestClient.bindToServer(new JdkClientHttpRequestFactory(httpClient))
				.baseUrl("https://localhost:8443")
				.build();
		restTestClient.get().uri("/actuator/info").exchange().expectStatus().is2xxSuccessful();
	}

	@Test
	void invalidNameGroup() {
		final SslBundle sslBundle  = sslBundles.getBundle("client");
		final HttpClient httpClient = buildWithNamedGroup("BAD", sslBundle);

		final RestTestClient restTestClient = RestTestClient.bindToServer(new JdkClientHttpRequestFactory(httpClient))
				.baseUrl("https://localhost:8443")
				.build();

		assertThatThrownBy(() -> restTestClient.get().uri("/actuator/info").exchange());
	}


	private HttpClient buildWithNamedGroup(final String namedGroup, final SslBundle sslBundle) {
		final SSLParameters sslParameters = new SSLParameters();
		sslParameters.setNamedGroups(new String[]{namedGroup});
		return HttpClient.newBuilder()
				.sslContext(sslBundle.createSslContext())
				.sslParameters(sslParameters)
				.build();
	}
}
