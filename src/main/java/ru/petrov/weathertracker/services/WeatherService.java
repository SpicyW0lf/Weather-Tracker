package ru.petrov.weathertracker.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.petrov.weathertracker.DTO.LocationDTO;
import ru.petrov.weathertracker.models.Location;
import ru.petrov.weathertracker.models.User;
import ru.petrov.weathertracker.repositories.LocationRepository;
import ru.petrov.weathertracker.repositories.UserRepository;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Value("${weather.key}")
    private String apiKey;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;

    public Set<LocationDTO> findLocations(String name) throws URISyntaxException, IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(String.format("http://api.openweathermap.org/geo/1.0/direct?q=%s&limit=5&appid=%s", name, apiKey)))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        Set<LocationDTO> locations = objectMapper.readValue(response.body(), new TypeReference<>(){});

        return locations;
    }

    public void saveLocation(LocationDTO locationDTO) {
        if (locationRepository.existsLocationByLatitudeAndLongitude(locationDTO.getLatitude(), locationDTO.getLongitude())) {
            throw new IllegalArgumentException("This location is already added");
        }

        locationRepository.save(locationDTO.toLocation());
    }

    public Set<LocationDTO> findAll() {
        return locationRepository.findAll().stream()
                .map(Location::toDto)
                .collect(Collectors.toSet());
    }

    public Set<LocationDTO> findUserLocs(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return user.getLocations().stream()
                .map(Location::toDto)
                .collect(Collectors.toSet());
    }

    public void saveLocToUser(String username, int locId) throws UsernameNotFoundException {
        Set<Location> locs;
        Set<User> users;
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Location location = locationRepository.findById(locId)
                .orElseThrow(() -> new NoSuchElementException("Location not found"));

        locs = user.getLocations();
        users = location.getUsers();
        locs.add(location);
        users.add(user);
        user.setLocations(locs);
        location.setUsers(users);
        userRepository.save(user);
        locationRepository.save(location);
    }
}