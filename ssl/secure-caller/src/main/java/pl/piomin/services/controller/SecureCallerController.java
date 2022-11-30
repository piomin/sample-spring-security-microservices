package pl.piomin.services.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class SecureCallerController {

    RestTemplate restTemplate;

    public SecureCallerController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/caller")
    public String call() {
        return "I'm `secure-caller`! calling... " +
                restTemplate.getForObject("https://localhost:8443/callme", String.class);
    }
}
