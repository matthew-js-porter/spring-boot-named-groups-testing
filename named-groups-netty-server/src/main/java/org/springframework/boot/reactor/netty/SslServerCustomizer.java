//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.springframework.boot.reactor.netty;

import io.netty.handler.ssl.ClientAuth;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslOptions;
import org.springframework.boot.web.server.Http2;
import org.springframework.boot.web.server.Ssl;
import reactor.netty.http.Http11SslContextSpec;
import reactor.netty.http.Http2SslContextSpec;
import reactor.netty.http.server.HttpServer;
import reactor.netty.tcp.AbstractProtocolSslContextSpec;
import reactor.netty.tcp.SslProvider;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;

public class SslServerCustomizer implements NettyServerCustomizer {
    private static final Log logger = LogFactory.getLog(SslServerCustomizer.class);
    private final @Nullable Http2 http2;
    private final ClientAuth clientAuth;
    private volatile SslProvider sslProvider;
    private final Map<String, SslProvider> serverNameSslProviders;

    public SslServerCustomizer(@Nullable Http2 http2, Ssl.@Nullable ClientAuth clientAuth, SslBundle sslBundle, Map<String, SslBundle> serverNameSslBundles) {
        this.http2 = http2;
        this.clientAuth = (ClientAuth)org.springframework.boot.web.server.Ssl.ClientAuth.map(clientAuth, ClientAuth.NONE, ClientAuth.OPTIONAL, ClientAuth.REQUIRE);
        this.sslProvider = this.createSslProvider(sslBundle);
        this.serverNameSslProviders = this.createServerNameSslProviders(serverNameSslBundles);
        this.updateSslBundle((String)null, sslBundle);
    }

    public HttpServer apply(HttpServer server) {
        return server.secure(this::applySecurity);
    }

    private void applySecurity(SslProvider.SslContextSpec spec) {
        spec.sslContext(this.sslProvider.getSslContext()).setSniAsyncMappings((serverName, promise) -> {
            SslProvider provider = serverName != null ? (SslProvider)this.serverNameSslProviders.get(serverName) : this.sslProvider;
            return promise.setSuccess(provider);
        });
    }

    void updateSslBundle(@Nullable String serverName, SslBundle sslBundle) {
        logger.debug("SSL Bundle has been updated, reloading SSL configuration");
        if (serverName == null) {
            this.sslProvider = this.createSslProvider(sslBundle);
        } else {
            this.serverNameSslProviders.put(serverName, this.createSslProvider(sslBundle));
        }

    }

    private Map<String, SslProvider> createServerNameSslProviders(Map<String, SslBundle> serverNameSslBundles) {
        Map<String, SslProvider> serverNameSslProviders = new HashMap();
        serverNameSslBundles.forEach((serverName, sslBundle) -> serverNameSslProviders.put(serverName, this.createSslProvider(sslBundle)));
        return serverNameSslProviders;
    }

    private SslProvider createSslProvider(SslBundle sslBundle) {
        return SslProvider.builder().sslContext(this.createSslContextSpec(sslBundle))
                .handlerConfigurator(sslHandler -> {
                    final SSLEngine engine = sslHandler.engine();
                    final SSLParameters sslParameters = engine.getSSLParameters();
                    sslParameters.setNamedGroups(new String[]{ "secp256r1" });
                    engine.setSSLParameters(sslParameters);
                })
                .build();
    }

    protected final AbstractProtocolSslContextSpec<?> createSslContextSpec(SslBundle sslBundle) {
        AbstractProtocolSslContextSpec<?> sslContextSpec = (AbstractProtocolSslContextSpec<?>)(this.http2 != null
                && this.http2.isEnabled() ? Http2SslContextSpec.forServer(sslBundle.getManagers().getKeyManagerFactory())
                : Http11SslContextSpec.forServer(sslBundle.getManagers().getKeyManagerFactory()));

        return sslContextSpec.configure((builder) -> {
            builder.trustManager(sslBundle.getManagers().getTrustManagerFactory());
            SslOptions options = sslBundle.getOptions();
            builder.protocols(options.getEnabledProtocols());
            builder.ciphers(SslOptions.asSet(options.getCiphers()));
            builder.clientAuth(this.clientAuth);
        });
    }
}
