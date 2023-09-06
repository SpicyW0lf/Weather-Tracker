package ru.petrov.weathertracker.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.petrov.weathertracker.DTO.LocationDTO;
import ru.petrov.weathertracker.DTO.weatherView.LocationView;
import ru.petrov.weathertracker.DTO.weatherView.Main;
import ru.petrov.weathertracker.DTO.weatherView.Weather;
import ru.petrov.weathertracker.DTO.weatherView.Wind;
import ru.petrov.weathertracker.services.WeatherService;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.ArrayList;
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

    @GetMapping("/main")
    public String getUserLocations(Principal principal, Model model) {
        Set<LocationView> locations = weatherService.findUserLocs(principal.getName());
        locations.add(new LocationView(
                1,
                new ArrayList<Weather>(){{add(new Weather("Clouds"));}},
                new Main(25.0),
                new Wind(3.25),
                "Moscow"
        ));
        locations.add(new LocationView(
                2,
                new ArrayList<Weather>(){{add(new Weather("Clouds"));}},
                new Main(25.0),
                new Wind(3.25),
                "Moscow"
        ));
        model.addAttribute("locations", locations);
        model.addAttribute("username", principal.getName());

        return "main";
    }

    @PostMapping("/locations/{id}")
    public String addLocation(@PathVariable("id") int locId, Principal principal) {
        weatherService.saveLocToUser(principal.getName(), locId);

        return "redirect:/user-locations";
    }

    @PostMapping("/locations")
    public String saveLocation(@RequestBody LocationDTO location) {
        weatherService.saveLocation(location);

        return "redirect:/user-locations";
    }
}
