//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.springframework.boot.tomcat;

import java.util.Map;
import org.apache.catalina.connector.Connector;
import org.apache.commons.logging.Log;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.apache.tomcat.util.net.SSLHostConfigCertificate.Type;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundleKey;
import org.springframework.boot.ssl.SslOptions;
import org.springframework.boot.ssl.SslStoreBundle;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.Ssl.ClientAuth;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class SslConnectorCustomizer {
    private final Log logger;
    private final Ssl.@Nullable ClientAuth clientAuth;
    private final Connector connector;

    public SslConnectorCustomizer(Log logger, Connector connector, Ssl.@Nullable ClientAuth clientAuth) {
        this.logger = logger;
        this.clientAuth = clientAuth;
        this.connector = connector;
    }

    public void update(@Nullable String serverName, SslBundle updatedSslBundle) {
        AbstractHttp11Protocol<?> protocol = (AbstractHttp11Protocol)this.connector.getProtocolHandler();
        String host = serverName != null ? serverName : protocol.getDefaultSSLHostConfigName();
        this.logger.debug("SSL Bundle for host " + host + " has been updated, reloading SSL configuration");
        this.addSslHostConfig(protocol, host, updatedSslBundle);
    }

    public void customize(SslBundle sslBundle, Map<String, SslBundle> serverNameSslBundles) {
        ProtocolHandler handler = this.connector.getProtocolHandler();
        Assert.state(handler instanceof AbstractHttp11Protocol, "To use SSL, the connector's protocol handler must be an AbstractHttp11Protocol subclass");
        this.configureSsl((AbstractHttp11Protocol)handler, sslBundle, serverNameSslBundles);
        this.connector.setScheme("https");
        this.connector.setSecure(true);
    }

    private void configureSsl(AbstractHttp11Protocol<?> protocol, @Nullable SslBundle sslBundle, Map<String, SslBundle> serverNameSslBundles) {
        protocol.setSSLEnabled(true);
        if (sslBundle != null) {
            this.addSslHostConfig(protocol, protocol.getDefaultSSLHostConfigName(), sslBundle);
        }

        serverNameSslBundles.forEach((serverName, bundle) -> this.addSslHostConfig(protocol, serverName, bundle));
    }

    private void addSslHostConfig(AbstractHttp11Protocol<?> protocol, String serverName, SslBundle sslBundle) {
        SSLHostConfig sslHostConfig = new SSLHostConfig();
        sslHostConfig.setHostName(serverName);
        this.configureSslClientAuth(sslHostConfig);
        this.applySslBundle(protocol, sslHostConfig, sslBundle);
        protocol.addSslHostConfig(sslHostConfig, true);
    }

    private void applySslBundle(AbstractHttp11Protocol<?> protocol, SSLHostConfig sslHostConfig, SslBundle sslBundle) {
        SslBundleKey key = sslBundle.getKey();
        SslStoreBundle stores = sslBundle.getStores();
        SslOptions options = sslBundle.getOptions();
        sslHostConfig.setSslProtocol(sslBundle.getProtocol());
        SSLHostConfigCertificate certificate = new SSLHostConfigCertificate(sslHostConfig, Type.UNDEFINED);
        String keystorePassword = stores.getKeyStorePassword() != null ? stores.getKeyStorePassword() : "";
        certificate.setCertificateKeystorePassword(keystorePassword);
        if (key.getPassword() != null) {
            certificate.setCertificateKeyPassword(key.getPassword());
        }

        if (key.getAlias() != null) {
            certificate.setCertificateKeyAlias(key.getAlias());
        }

        sslHostConfig.addCertificate(certificate);
        if (options.getCiphers() != null) {
            String ciphers = StringUtils.arrayToCommaDelimitedString(options.getCiphers());
            sslHostConfig.setCiphers(ciphers);
        }

        this.configureSslStores(sslHostConfig, certificate, stores);
        this.configureEnabledProtocols(sslHostConfig, options);
    }

    private void configureEnabledProtocols(SSLHostConfig sslHostConfig, SslOptions options) {
        if (options.getEnabledProtocols() != null) {
            String enabledProtocols = StringUtils.arrayToDelimitedString(options.getEnabledProtocols(), "+");
            sslHostConfig.setProtocols(enabledProtocols);
        }
        sslHostConfig.setGroups("secp256r1");

    }

    private void configureSslClientAuth(SSLHostConfig config) {
        config.setCertificateVerification((String)ClientAuth.map(this.clientAuth, "none", "optional", "required"));
    }

    private void configureSslStores(SSLHostConfig sslHostConfig, SSLHostConfigCertificate certificate, SslStoreBundle stores) {
        try {
            if (stores.getKeyStore() != null) {
                certificate.setCertificateKeystore(stores.getKeyStore());
            }

            if (stores.getTrustStore() != null) {
                sslHostConfig.setTrustStore(stores.getTrustStore());
            }

        } catch (Exception ex) {
            throw new IllegalStateException("Could not load store: " + ex.getMessage(), ex);
        }
    }
}
