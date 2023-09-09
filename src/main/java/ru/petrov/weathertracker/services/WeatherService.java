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
        Set<LocationDTO> locations = objectMapper.readValue(httpOpenAPIClient.findLocationsByName(name), new TypeReference<>() {
        });

        return locations;
    }

    public Location saveLocation(LocationDTO locationDTO) {
        if (locationRepository.findLocationByLatitudeAndLongitude(locationDTO.getLatitude(), locationDTO.getLongitude()).isPresent()) {
            throw new IllegalArgumentException("This location is already added");
        }

        return locationRepository.save(locationDTO.toLocation());
    }

    public Optional<LocationView> getWeather(Double latitude, Double longitude) {
        try {
            LocationView locs = objectMapper.readValue(
                    httpOpenAPIClient.findLocationByLL(latitude, longitude),
                    LocationView.class
            );

            return Optional.of(locs);
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    public Set<LocationDTO> findUserLocs(String username)  {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Set<LocationDTO> userLocs = user.getLocations().stream()
                .map(Location::toDto)
                .collect(Collectors.toSet());

        return userLocs;
    }

    public void saveLocToUser(String username, LocationDTO locationDTO) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Location location = locationRepository.findLocationByLatitudeAndLongitude(
                locationDTO.getLatitude(),
                locationDTO.getLongitude()
                        )
                .orElse(saveLocation(locationDTO));
        Set<Location> locs = user.getLocations();
        Set<User> users = location.getUsers();

        locs.add(location);
        users.add(user);
        user.setLocations(locs);
        location.setUsers(users);

        userRepository.save(user);
        locationRepository.save(location);
    }

    public void deleteLocation(int id, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Location location = locationRepository.findById(id)
                .orElseThrow(RuntimeException::new);
        Set<Location> locs = user.getLocations();
        Set<User> users = location.getUsers();
        System.out.println(locs);
        locs.remove(location);
        System.out.println(locs);
        users.remove(user);
        user.setLocations(locs);

        if (users.size() == 0) {
            locationRepository.delete(location);
        } else {
            location.setUsers(users);
            locationRepository.save(location);
        }
    }
}