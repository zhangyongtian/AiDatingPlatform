package com.dating.platform.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class TestController {
    
    @GetMapping("/test")
    public String test() {
        return "Hello World!";
    }
} 