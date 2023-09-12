package ru.petrov.weathertracker.services;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import ru.petrov.weathertracker.DTO.LocationDTO;
import ru.petrov.weathertracker.models.Location;
import ru.petrov.weathertracker.models.User;
import ru.petrov.weathertracker.repositories.LocationRepository;
import ru.petrov.weathertracker.repositories.UserRepository;
import ru.petrov.weathertracker.utils.OpenAPIClient;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WeatherServiceTest {
    @LocalServerPort
    Integer port;

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:15-alpine"
    );

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @BeforeEach
    void beforeEach() {
        locationRepository.deleteAll();
        userRepository.deleteAll();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @MockBean
    OpenAPIClient client;
    @Autowired
    LocationRepository locationRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    WeatherService weatherService;

    @Test
    void findLocations_shouldReturnTwoLocs() throws URISyntaxException, IOException, InterruptedException {
        String zap = """
                [
                {
                    "name": "Moscow",
                    "lat": "1.12",
                    "lon": "1.11",
                    "country": "RU"
                },
                {
                    "name": "Moscow",
                    "lat": "1.12",
                    "lon": "1.113",
                    "country": "RU"
                }
                ]""";

        doReturn(zap).when(client).findLocationsByName("Moscow");

        Set<LocationDTO> locations = weatherService.findLocations("Moscow");

        assertEquals(2, locations.size());
    }

    @Test
    void saveLocation_withNewLocation_shouldSaveLocation() {
        LocationDTO dto = new LocationDTO(
                null,
                "Pushkin",
                1.12,
                2.13,
                "Russia"
        );

        Location location = weatherService.saveLocation(dto);

        assertEquals(dto.getLatitude(), location.getLatitude());
        assertEquals(dto.getName(), location.getName());
    }

    @Test
    void saveLocation_withExistingLocation_shouldThrowIllegalArgumentException() {
        LocationDTO dto = new LocationDTO(
                null,
                "Pushkin",
                1.12,
                2.13,
                "Russia"
        );
        locationRepository.save(dto.toLocation());

        assertThrows(IllegalArgumentException.class, () -> weatherService.saveLocation(dto));
    }

    @Test
    void getWeather_withBadCoordinates_shouldThrowException() throws URISyntaxException, IOException, InterruptedException {
        doThrow(IOException.class).when(client).findLocationByLL(1.1, 2.2);

        assertTrue(weatherService.getWeather(1.1, 2.2).isEmpty());
    }

    @Test
    void getWeather_withGoodCoordinates_shouldReturnPresentOptional() throws URISyntaxException, IOException, InterruptedException {
        String str = """
                {
                    "weather": [{"main": "r"}],
                    "main": {"temp": 1.22},
                    "wind": {"speed": 1.22},
                    "name": "ru"
                }
                    """;
        doReturn(str).when(client).findLocationByLL(1.1, 2.2);

        assertTrue(weatherService.getWeather(1.1, 2.2).isPresent());
    }

    @Test
    void findUserLocs_withWrongUser_shouldThrowUsernameNotFoundException() {
        userRepository.save(new User("Dima", "doma"));

        assertThrows(UsernameNotFoundException.class, () -> weatherService.findUserLocs("Kolya"));
    }

    @Test
    void findUserLocs_withExistsUser_shouldReturnSet() {
        User user = new User("Dima", "doma");
        user.getLocations().add(new Location("Pov", 1.1, 1.2, "rus"));

        userRepository.save(user);

        assertEquals(1, weatherService.findUserLocs("Dima").size());
    }

    @Test
    void saveLocToUser_withWrongUsername_shouldThrowUsernameNotFoundException() {
        userRepository.save(new User("Dima", "doma"));

        assertThrows(UsernameNotFoundException.class, () -> weatherService.saveLocToUser("Kola", new LocationDTO()));
    }

    @Test
    void saveLocToUser_withGoodUsernameAndNewLoc_shouldCreateLocAndSave() {
        userRepository.save(new User("Doma", "doma"));
        weatherService.saveLocToUser("Doma", new LocationDTO(100, "pov", 1.1, 1.2, "Ru"));

        assertEquals("Ru", locationRepository.findByName("pov").get().getCountry());
        assertEquals(1, userRepository.findByUsername("Doma").get()
                .getLocations()
                .size()
        );
        assertEquals(1, locationRepository.findByName("pov").get().getUsers().size());
    }
}