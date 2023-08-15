package ru.petrov.weathertracker.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.LinkedList;

@Controller
public class FirstController {

    @GetMapping("/home")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Привет");
    }
}
