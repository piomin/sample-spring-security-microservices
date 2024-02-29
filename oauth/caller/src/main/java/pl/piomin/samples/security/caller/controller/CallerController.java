package pl.piomin.samples.security.caller.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/caller")
public class CallerController {

    private WebClient webClient;

    public CallerController(WebClient webClient) {
        this.webClient = webClient;
    }

    @PreAuthorize("hasAuthority('SCOPE_TEST')")
    @GetMapping("/ping")
    public String ping() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();

        String scopes = webClient
                .get()
                .uri("http://localhost:8040/callme/ping")
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return "Callme scopes: " + scopes;
    }
}
