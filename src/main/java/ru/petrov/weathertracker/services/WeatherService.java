package ru.petrov.weathertracker.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.petrov.weathertracker.DTO.LocationDTO;
import ru.petrov.weathertracker.models.Location;
import ru.petrov.weathertracker.repositories.LocationRepository;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Value("${weather.key}")
    private String apiKey;
    private final LocationRepository locationRepository;

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
}