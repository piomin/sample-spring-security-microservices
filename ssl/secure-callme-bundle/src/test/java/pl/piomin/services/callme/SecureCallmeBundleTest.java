package pl.piomin.services.callme;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class SecureCallmeBundleTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void call() {
        String res = restTemplate.getForObject("/callme", String.class);
        assertEquals("I'm secure-callme-bundle!", res);
    }
}
