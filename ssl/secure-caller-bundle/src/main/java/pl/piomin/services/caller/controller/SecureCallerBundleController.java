package pl.piomin.services.caller.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@RestController
public class SecureCallerBundleController {

    RestTemplate restTemplate;
    RestClient restClient;

    @Value("${client.url}")
    String clientUrl;

    public SecureCallerBundleController(RestTemplate restTemplate, RestClient restClient) {
        this.restTemplate = restTemplate;
        this.restClient = restClient;
    }

    @GetMapping("/caller")
    public String call() {
        return "I'm `secure-caller`! calling... " +
                restTemplate.getForObject(clientUrl, String.class);
    }

    @GetMapping("/caller/client")
    public String call2() {
        return "I'm `secure-caller`! calling... " +
                restClient.get().retrieve().body(String.class);
    }
}
