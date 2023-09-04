package ru.petrov.weathertracker.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class OpenAPIClient {
    @Value("${weather.key}")
    private String apiKey;
    private final HttpClient client = HttpClient.newHttpClient();
    private final String apiAllUrl = "http://api.openweathermap.org/geo/1.0/direct?q=%s&limit=5&appid=%s";
    private final String apiOneUrl = "https://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&appid=%s";

    public String findLocationsByName(String name) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(String.format(apiAllUrl, name, apiKey)))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

    public String findLocationByLL(Double latitude, Double longitude) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(String.format(apiOneUrl, latitude, longitude, apiKey)))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }
}
