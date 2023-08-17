package ru.petrov.weathertracker.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.petrov.weathertracker.DTO.LocationDTO;
import ru.petrov.weathertracker.services.WeatherService;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Set;

@Controller
@AllArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping("/locations-all")
    @ResponseBody
    public ResponseEntity<Set<LocationDTO>> getLocations(@RequestParam String name) throws URISyntaxException, IOException, InterruptedException {
        Set<LocationDTO> locations = weatherService.findLocations(name);

        return new ResponseEntity<>(locations, HttpStatus.OK);
    }

    @GetMapping("/user-locations")
    @ResponseBody
    public ResponseEntity<Set<LocationDTO>> getUserLocations() {
        Set<LocationDTO> locations = weatherService.findAll();

        return new ResponseEntity<>(locations, HttpStatus.OK);
    }

    @PostMapping("/locations")
    public String saveLocation(@RequestBody LocationDTO location) {
        weatherService.saveLocation(location);

        return "redirect:/user-locations";
    }
}
