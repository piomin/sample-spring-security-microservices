package pl.piomin.samples.security.gateway;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GatewayApplicationTests {

    static String accessToken;

    @Autowired
    TestRestTemplate restTemplate;
    @LocalServerPort
    Integer port;

    @Container
    static KeycloakContainer keycloak = new KeycloakContainer()
            .withRealmImportFile("realm-export.json")
            .withExposedPorts(8080);

    @Container
    static GenericContainer callme = new GenericContainer("sample-spring-security-microservices/callme:1.1-SNAPSHOT")
            .withExposedPorts(8080);

    @DynamicPropertySource
    static void registerResourceServerIssuerProperty(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.client.provider.keycloak.issuer-uri",
                () -> keycloak.getAuthServerUrl() + "/realms/demo");
        registry.add("spring.cloud.gateway.routes[0].uri",
                () -> "http://localhost:" + callme.getFirstMappedPort());
        registry.add("spring.cloud.gateway.routes[0].id", () -> "callme-service");
        registry.add("spring.cloud.gateway.routes[0].predicates[0]", () -> "Path=/callme/**");
        registry.add("spring.cloud.gateway.routes[0].filters[0]", () -> "RemoveRequestHeader=Cookie");
    }

//    @BeforeAll
//    static void init() {
//        System.setProperty("spring.security.oauth2.client.provider.keycloak.token-uri",
//                "http://localhost:" + keycloak.getFirstMappedPort() + "/auth/realms/demo/protocol/openid-connect/token");
//        System.setProperty("spring.security.oauth2.client.provider.keycloak.authorization-uri",
//                "http://localhost:" + keycloak.getFirstMappedPort() + "/auth/realms/demo/protocol/openid-connect/auth");
//        System.setProperty("spring.security.oauth2.client.provider.keycloak.userinfo-uri",
//                "http://localhost:" + keycloak.getFirstMappedPort() + "/auth/realms/demo/protocol/openid-connect/userinfo");
//        System.setProperty("spring.cloud.gateway.routes[0].uri",
//                "http://localhost:" + callme.getFirstMappedPort());
//        System.setProperty("spring.cloud.gateway.routes[0].id", "callme-service");
//        System.setProperty("spring.cloud.gateway.routes[0].predicates[0]", "Path=/callme/**");
//        System.setProperty("spring.cloud.gateway.routes[0].filters[0]", "RemoveRequestHeader=Cookie");
//    }

    @Test
    @Order(1)
    void shouldStart() {

    }

    @Test
    @Order(1)
    void shouldBeRedirectedToLoginPage() {
        ResponseEntity<String> r = restTemplate.getForEntity("/callme/ping", String.class);
        assertEquals(200, r.getStatusCode().value());
        assertTrue(r.getBody().contains("Sign in to your account"));
    }

    @Test
    @Order(2)
    void shouldObtainAccessToken() throws URISyntaxException {
        URI authorizationURI = new URIBuilder(keycloak.getAuthServerUrl() + "/realms/demo/protocol/openid-connect/token").build();
        WebClient webclient = WebClient.builder().build();
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.put("grant_type", Collections.singletonList("password"));
        formData.put("client_id", Collections.singletonList("spring-with-test-scope"));
        formData.put("username", Collections.singletonList("spring"));
        formData.put("password", Collections.singletonList("Spring_123"));

        String result = webclient.post()
                .uri(authorizationURI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(String.class)
                .block();
        JacksonJsonParser jsonParser = new JacksonJsonParser();
        accessToken = jsonParser.parseMap(result)
                .get("access_token")
                .toString();
        assertNotNull(accessToken);
    }

    @Test
    @Order(3)
    void shouldReturnToken() {
        System.out.println("!!!!!!!" + accessToken);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Object> entity = new HttpEntity<>(headers);
        WebClient webclient = WebClient.builder().build();
        String body = webclient.get().uri("http://localhost:" + port + "/token")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        System.out.println("!!!!!!!" + body);
//        ResponseEntity<String> r = restTemplate.exchange("/callme/ping", HttpMethod.GET, entity, String.class);
//        assertEquals(200, r.getStatusCode().value());
        assertNotNull(body);
    }
}
