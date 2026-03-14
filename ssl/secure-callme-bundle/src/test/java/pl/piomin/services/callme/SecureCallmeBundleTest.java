package pl.piomin.services.callme;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
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
        RestTemplate restTemplate(RestTemplateBuilder builder, SslBundles sslBundles) {
            return builder.sslBundle(sslBundles.getBundle("server")).build();
        }
    }
}
