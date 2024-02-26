package pl.piomin.samples.security.gateway;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class GatewayApplicationTests {

    @Autowired
    RestTemplate restTemplate;
    @LocalServerPort
    Integer port;

    @Container
    static KeycloakContainer keycloak = new KeycloakContainer()
            .withRealmImportFile("test-realm.json")
            .withExposedPorts(8080);

    @Container
    static GenericContainer callme = new GenericContainer("sample-spring-security-microservices/callme:1.1-SNAPSHOT")
            .withExposedPorts(8080);

    @BeforeAll
    static void init() {
        System.setProperty("spring.security.oauth2.client.provider.keycloak.token-uri",
                "http://localhost:" + keycloak.getFirstMappedPort() + "/auth/realms/demo/protocol/openid-connect/token");
        System.setProperty("spring.security.oauth2.client.provider.keycloak.authorization-uri",
                "http://localhost:" + keycloak.getFirstMappedPort() + "/auth/realms/demo/protocol/openid-connect/auth");
        System.setProperty("spring.security.oauth2.client.provider.keycloak.userinfo-uri",
                "http://localhost:" + keycloak.getFirstMappedPort() + "/auth/realms/demo/protocol/openid-connect/userinfo");
        System.setProperty("spring.cloud.gateway.routes[0].uri",
                "http://localhost:" + callme.getFirstMappedPort());
        System.setProperty("spring.cloud.gateway.routes[0].id", "callme-service");
        System.setProperty("spring.cloud.gateway.routes[0].predicates[0]", "Path=/callme/**");
        System.setProperty("spring.cloud.gateway.routes[0].filters[0]", "RemoveRequestHeader=Cookie");
    }

    @Test
    void shouldStart() {

    }

    @Test
    void shouldCall() {
        ResponseEntity<String> r = restTemplate.getForEntity("http://localhost:" + port + "/callme", String.class);
        assertEquals(200, r.getStatusCode().value());
        assertTrue(r.getBody().contains("Login with OAuth 2.0"));
    }

//    @Test
    void shouldGetToken() {
        ResponseEntity<String> r = restTemplate.getForEntity("/token", String.class);
        assertEquals(200, r.getStatusCode().value());
        assertNotNull(r.getBody());
    }
}
