package ru.petrov.weathertracker.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.petrov.weathertracker.DTO.LocationDTO;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Set;

@Service
public class WeatherService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Value("${weather.key}")
    private String apiKey;

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
}