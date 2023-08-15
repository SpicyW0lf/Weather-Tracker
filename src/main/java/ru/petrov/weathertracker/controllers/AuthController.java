package ru.petrov.weathertracker.controllers;

import org.springframework.web.bind.annotation.GetMapping;

public class AuthController {
    @GetMapping("/register")
    public String register() {
        return "redirect:login";
    }
}
