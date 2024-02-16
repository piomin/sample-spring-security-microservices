package pl.piomin.services.caller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;


@SpringBootApplication
public class SecureCallerBundle {

    public static void main(String[] args) {
        SpringApplication.run(SecureCallerBundle.class, args);
    }

    @Bean
    RestTemplate builder(RestTemplateBuilder builder, SslBundles sslBundles) {
        return builder.setSslBundle(sslBundles.getBundle("client")).build();
    }
}
