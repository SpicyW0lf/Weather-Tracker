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
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
    public String mainPage(Principal principal, Model model, @RequestParam(required = false) String city) throws URISyntaxException, IOException, InterruptedException {
        Set<LocationDTO> locationsFound = new HashSet<>();
        Set<LocationView> locations = new HashSet<>();

        if (city == null) {
            locations = weatherService.findUserLocs(principal.getName()).stream()
                    .map((loc) -> {
                        LocationView view = weatherService.getWeather(loc.getLatitude(), loc.getLongitude());
                        view.setId(loc.getId());
                        return view;
                    })
                    .collect(Collectors.toSet());
        } else {
            locationsFound = weatherService.findLocations(city);
        }

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
        model.addAttribute("locationsFound", locationsFound);
        model.addAttribute("city", city);
        model.addAttribute("newLocation", new LocationDTO());

        return "main";
    }

    @PostMapping("/add-loc")
    public String addLocation(Double latitude, Double longitude, Principal principal) {
        System.out.println(latitude);
        System.out.println(longitude);
        //weatherService.saveLocToUser(principal.getName(), locId);

        return "redirect:/main";
    }

    @PostMapping("/locations")
    public String saveLocation(@RequestBody LocationDTO location) {
        weatherService.saveLocation(location);

        return "redirect:/user-locations";
    }
}
