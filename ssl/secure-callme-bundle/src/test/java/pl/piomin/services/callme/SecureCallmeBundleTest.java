package pl.piomin.services.callme;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;

import java.net.http.HttpClient;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SecureCallmeBundleTest {

    @Autowired
    RestTemplate restTemplate;
    @LocalServerPort
    Integer port;

    @Test
    void call() {
        String res = restTemplate.getForObject("https://localhost:" + port + "/callme", String.class);
        assertEquals("I'm secure-callme-bundle!", res);
    }

    @TestConfiguration
    public static class TestRestTemplateConfiguration {
        @Bean
        @Primary
        RestTemplate testRestTemplate(RestTemplateBuilder builder, SslBundles sslBundles) {
            builder.setSslBundle(sslBundles.getBundle("server"));
            return builder.build();
        }
    }
}
