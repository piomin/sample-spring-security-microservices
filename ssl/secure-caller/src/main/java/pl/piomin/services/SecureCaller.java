package pl.piomin.services;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import pl.piomin.services.configuration.ClientSSLProperties;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;


@SpringBootApplication
public class SecureCaller {

    public static void main(String[] args) {
        SpringApplication.run(SecureCaller.class, args);
    }

    @Autowired
    ClientSSLProperties clientSSLProperties;

    @Bean
    RestTemplate builder(RestTemplateBuilder builder) throws GeneralSecurityException, IOException {
        final SSLContext sslContext = new SSLContextBuilder()
                .loadTrustMaterial(
                        new File(clientSSLProperties.getTrustStore()),
                        clientSSLProperties.getTrustStorePassword().toCharArray())
                .loadKeyMaterial(
                        new File(clientSSLProperties.getKeyStore()),
                        clientSSLProperties.getKeyStorePassword().toCharArray(),
                        clientSSLProperties.getKeyStorePassword().toCharArray()
                )
                .build();

        final SSLConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactoryBuilder.create()
                .setSslContext(sslContext)
                .build();

        final HttpClientConnectionManager cm = PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(sslSocketFactory)
                .build();

        final HttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .evictExpiredConnections()
                .build();


        return builder
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory(httpClient))
                .build();
    }
}
