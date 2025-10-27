package com.example.security.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Welcome to Spring Security Example!";
    }

    @GetMapping("/public")
    public String publicPage() {
        return "This is a public page accessible to everyone.";
    }

    @GetMapping("/admin")
    public String adminPage() {
        return "This is an admin page - only admins can see this.";
    }

    @GetMapping("/user")
    public String userPage() {
        return "This is a user page - authenticated users can see this.";
    }
}