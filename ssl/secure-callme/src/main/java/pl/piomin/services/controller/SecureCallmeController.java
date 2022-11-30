package pl.piomin.services.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecureCallmeController {

    @GetMapping("/callme")
    public String call() {
        return "I'm `secure-callme`!";
    }

}
