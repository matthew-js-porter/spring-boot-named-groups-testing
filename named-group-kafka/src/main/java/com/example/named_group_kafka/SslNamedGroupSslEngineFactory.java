//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.example.named_group_kafka;

import java.security.KeyStore;
import java.util.Map;
import java.util.Set;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import org.apache.kafka.common.security.auth.SslEngineFactory;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslStoreBundle;
import org.springframework.util.Assert;

public class SslNamedGroupSslEngineFactory implements SslEngineFactory {
    private @Nullable Map<String, ?> configs;
    private volatile @Nullable SslBundle sslBundle;
    private volatile String[] namedGroups;

    public void configure(Map<String, ?> configs) {
        this.configs = configs;
        KeyStore keyStore;
        try {
            keyStore = KeyStore.getInstance("JKS");
            keyStore.load(getClass().getResourceAsStream("/ssl/kafka.server.truststore.jks"), "changeit".toCharArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.sslBundle = SslBundle.of(SslStoreBundle.of(null, null, keyStore));
        this.namedGroups = ((String)configs.get("ssl.groups")).split(",");
    }

    public void close() {
    }

    public SSLEngine createClientSslEngine(String peerHost, int peerPort, String endpointIdentification) {
        SslBundle sslBundle = this.sslBundle;
        Assert.state(sslBundle != null, "'sslBundle' must not be null");
        SSLEngine sslEngine = sslBundle.createSslContext().createSSLEngine(peerHost, peerPort);
        sslEngine.setUseClientMode(true);
        SSLParameters sslParams = sslEngine.getSSLParameters();
        sslParams.setEndpointIdentificationAlgorithm(endpointIdentification);
        sslParams.setNamedGroups(this.namedGroups);
        sslEngine.setSSLParameters(sslParams);
        return sslEngine;
    }

    public SSLEngine createServerSslEngine(String peerHost, int peerPort) {
        SslBundle sslBundle = this.sslBundle;
        Assert.state(sslBundle != null, "'sslBundle' must not be null");
        SSLEngine sslEngine = sslBundle.createSslContext().createSSLEngine(peerHost, peerPort);
        sslEngine.setUseClientMode(false);
        return sslEngine;
    }

    public boolean shouldBeRebuilt(Map<String, Object> nextConfigs) {
        return !nextConfigs.equals(this.configs);
    }

    public Set<String> reconfigurableConfigs() {
        return Set.of("ssl.groups");
    }

    public @Nullable KeyStore keystore() {
        SslBundle sslBundle = this.sslBundle;
        Assert.state(sslBundle != null, "'sslBundle' must not be null");
        return sslBundle.getStores().getKeyStore();
    }

    public @Nullable KeyStore truststore() {
        SslBundle sslBundle = this.sslBundle;
        Assert.state(sslBundle != null, "'sslBundle' must not be null");
        return sslBundle.getStores().getTrustStore();
    }
}
