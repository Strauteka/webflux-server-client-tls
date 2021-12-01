package edu.strauteka.example.client;

import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.net.InetAddress;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class CustomWebClient {

    // file:clientkeystore.jks
    @Value("${client.keystore}")
    private Resource truststoreFile;

    @Value("${client.keystore.password}")
    private String resourceFilePassword;

    @Value("${server.port}")
    private Integer port;

    @Value("${server.address}")
    private String address;

    @Value("${server.ssl.enabled}")
    private Boolean sslEnabled;

    @SneakyThrows
    @Bean
    public WebClient webClient(/*Environment environment*/) {
//        final boolean sslEnabled = Boolean.parseBoolean(environment.getProperty("server.ssl.enabled"));
        return WebClient.builder()
                .baseUrl(baseUrl())
                .clientConnector(new ReactorClientHttpConnector(createHttpClient(port)))
                .defaultHeader(HttpHeaders.ACCEPT,
                        MediaType.APPLICATION_NDJSON_VALUE,
                        MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @SneakyThrows
    private HttpClient createHttpClient(int port) {
        HttpClient client = HttpClient.create()
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .responseTimeout(Duration.ofMillis(5000))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS)))
                .host(InetAddress.getLocalHost().getHostAddress())
                .port(port);

        if(sslEnabled) {
                client = client.secure(sslSpec -> sslSpec.sslContext(completeTruststoreProvider()));
        }
        return client;
    }

    private SslContext completeTruststoreProvider() {
        return isTruststoreProvided() ? createSslContextWithTruststore() : createDummySslContext();
    }

    private String baseUrl() {
        return String.format("http%s://%s:%d",
                (sslEnabled ? "s" : ""),
                address,
                port);
    }

    private boolean isTruststoreProvided() {
        return Objects.nonNull(truststoreFile) && Objects.nonNull(resourceFilePassword);
    }

    @SneakyThrows
    public SslContext createSslContextWithTruststore() {
        Objects.requireNonNull(truststoreFile, "Client Truststore file is null!");
        Objects.requireNonNull(resourceFilePassword, "Client Truststore password is null!");
        if (!truststoreFile.isReadable()) {
            throw new RuntimeException("Truststore File is not readable!");
        }
        //The Keystore
        KeyStore truststore = KeyStore.getInstance(KeyStore.getDefaultType());
        truststore.load(truststoreFile.getInputStream(), resourceFilePassword.toCharArray());

        //The TrustManagerFactory will query the KeyStore for information about which remote certificates should be trusted during authorization checks.
        TrustManagerFactory trustManagerFactory = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());

        //The primary responsibility of the KeyManager is to select the authentication credentials that will eventually be sent to the remote host
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(truststore, resourceFilePassword.toCharArray());

        trustManagerFactory.init(truststore);

        return SslContextBuilder.
                forClient()
                .trustManager(trustManagerFactory)
                .keyManager(keyManagerFactory)
                .keyStoreType("PKCS12")
                .protocols("TLSv1","TLSv1.2","TLSv1.1","TLSv1.3","SSLv3")
                .build();
    }

    @SneakyThrows
    public SslContext createDummySslContext() {
        return SslContextBuilder.
                forClient()
                //.trustManager(InsecureTrustManagerFactory.INSTANCE) //works also!
                .trustManager(DUMMY_TRUST_MANAGER)
                .keyStoreType("PKCS12")
                .build();
    }


    // Without truststore file
    private static final TrustManager DUMMY_TRUST_MANAGER = new X509TrustManager() {
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
            // Always trust - it is an example.
            // You should do something in the real world.
            // You will reach here only if you enabled client certificate auth,
            // as described in SecureChatSslContextFactory.
            log.warn("UNKNOWN CLIENT CERTIFICATE: {}", chain[0].getSubjectDN());
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
            // Always trust - it is an example.
            // You should do something in the real world.
            log.warn("UNKNOWN SERVER CERTIFICATE: {}", chain[0].getSubjectDN());
        }
    };
}
