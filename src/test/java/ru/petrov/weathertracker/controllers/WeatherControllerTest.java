package ru.petrov.weathertracker.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import ru.petrov.weathertracker.DTO.LocationDTO;
import ru.petrov.weathertracker.services.WeatherService;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class WeatherControllerTest {
    @Mock
    WeatherService weatherService;
    @InjectMocks
    WeatherController weatherController;

    @Test
    void getLocations_ReturnsValidResponseEntity() throws URISyntaxException, IOException, InterruptedException {
        Set<LocationDTO> locations = Set.of(
                new LocationDTO("Moscow", 22.44, -1.2, "Russia"),
                new LocationDTO("Moscow", 11.2, -2.3, "Prossia")
        );

        doReturn(locations).when(this.weatherService).findLocations("Moscow");
        var response = this.weatherController.getLocations("Moscow");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(locations, response.getBody());
    }

}