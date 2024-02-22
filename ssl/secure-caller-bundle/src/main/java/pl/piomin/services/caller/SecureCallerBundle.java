package pl.piomin.services.caller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.client.RestClientSsl;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.security.KeyStoreException;


@SpringBootApplication
public class SecureCallerBundle {

    private static final Logger LOG = LoggerFactory.getLogger(SecureCallerBundle.class);
    public static void main(String[] args) {
        SpringApplication.run(SecureCallerBundle.class, args);
    }

    @Autowired
    ApplicationContext context;

    @Bean("restTemplate")
    RestTemplate builder(RestTemplateBuilder builder, SslBundles sslBundles) {
        sslBundles.addBundleUpdateHandler("client", sslBundle -> {
            try {
                LOG.info("Bundle updated: " + sslBundle.getStores().getKeyStore().getCertificate("certificate"));
            } catch (KeyStoreException e) {
                LOG.error("Error on getting certificate", e);
            }
            DefaultSingletonBeanRegistry registry = (DefaultSingletonBeanRegistry) context.getAutowireCapableBeanFactory();
            registry.destroySingleton("restTemplate");
            registry.registerSingleton("restTemplate", builder.setSslBundle(sslBundle).build());
        });
        return builder.setSslBundle(sslBundles.getBundle("client")).build();
    }

    @Value("${client.url}")
    String clientUrl;

    @Bean
    RestClient restClient(RestClient.Builder restClientBuilder, RestClientSsl ssl) {
        return restClientBuilder.baseUrl(clientUrl)
                .apply(ssl.fromBundle("client"))
                .build();
    }
}
