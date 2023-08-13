package ru.petrov.weathertracker.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.LinkedList;

@Controller
public class FirstController {

    @GetMapping("/home")
    public ResponseEntity<?> hello() {
        return ResponseEntity.ok(200);
    }
}
