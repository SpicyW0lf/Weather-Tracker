package ru.petrov.weathertracker.controllers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.petrov.weathertracker.DTO.LocationDTO;
import ru.petrov.weathertracker.DTO.weatherView.LocationView;
import ru.petrov.weathertracker.services.WeatherService;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping("/main")
    public String mainPage(
            Principal principal,
            Model model,
            @RequestParam(required = false) String city
    ) throws URISyntaxException, IOException, InterruptedException {
        Set<LocationDTO> locationsFound = new HashSet<>();
        Set<LocationView> locations = new HashSet<>();

        if (city == null) {
            locations = weatherService.findUserLocs(principal.getName()).stream()
                    .map((loc) -> {
                        LocationView view = weatherService.getWeather(loc.getLatitude(), loc.getLongitude())
                                .orElseThrow(InternalError::new);
                        view.setId(loc.getId());
                        return view;
                    })
                    .collect(Collectors.toSet());
        } else {
            locationsFound = weatherService.findLocations(city);
        }

        model.addAttribute("locations", locations);
        model.addAttribute("username", principal.getName());
        model.addAttribute("locationsFound", locationsFound);
        model.addAttribute("city", city);
        model.addAttribute("newLocation", new LocationDTO());

        return "main";
    }

    @PostMapping("/add-loc")
    public String addLocation(LocationDTO location, Principal principal) {
        weatherService.saveLocToUser(principal.getName(), location);

        return "redirect:/main";
    }

    @DeleteMapping("/delete-loc")
    public String deleteLocation(int id, Principal principal) {
        weatherService.deleteLocation(id, principal.getName());

        return "redirect:/main";
    }
}
