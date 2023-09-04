package ru.petrov.weathertracker.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.petrov.weathertracker.DTO.LocationDTO;
import ru.petrov.weathertracker.DTO.weatherView.LocationView;
import ru.petrov.weathertracker.models.Location;
import ru.petrov.weathertracker.models.User;
import ru.petrov.weathertracker.repositories.LocationRepository;
import ru.petrov.weathertracker.repositories.UserRepository;
import ru.petrov.weathertracker.utils.OpenAPIClient;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final OpenAPIClient httpOpenAPIClient;

    public Set<LocationDTO> findLocations(String name) throws URISyntaxException, IOException, InterruptedException {
        return objectMapper.readValue(httpOpenAPIClient.findLocationsByName(name), new TypeReference<>() {
        });
    }

    public void saveLocation(LocationDTO locationDTO) {
        if (locationRepository.existsLocationByLatitudeAndLongitude(locationDTO.getLatitude(), locationDTO.getLongitude())) {
            throw new IllegalArgumentException("This location is already added");
        }

        locationRepository.save(locationDTO.toLocation());
    }

    public LocationView getWeather() throws URISyntaxException, IOException, InterruptedException {
        LocationView locs = objectMapper.readValue(
                httpOpenAPIClient.findLocationByLL(1.0, 3.0),
                LocationView.class
        );

        return locs;
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
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Location location = locationRepository.findById(locId)
                .orElseThrow(() -> new NoSuchElementException("Location not found"));

        Set<Location> locs = user.getLocations();
        Set<User> users = location.getUsers();
        locs.add(location);
        users.add(user);
        user.setLocations(locs);
        location.setUsers(users);
        userRepository.save(user);
        locationRepository.save(location);
    }
}