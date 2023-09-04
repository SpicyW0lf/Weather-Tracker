package ru.petrov.weathertracker.controllers;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import ru.petrov.weathertracker.models.User;
import ru.petrov.weathertracker.repositories.UserRepository;
import ru.petrov.weathertracker.security.RegisterService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(printOnlyOnFailure = false)
class AuthControllerIT {

    @LocalServerPort
    private Integer port;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    RegisterService registerService;

    @Autowired
    UserRepository userRepository;

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
        userRepository.deleteAll();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void addUser_shouldCreateNewUser() throws Exception {
        this.mockMvc.perform(
                        post("/register")
                                .param("username", "Dima")
                                .param("password", "1234")
                )
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void addUser_shouldNotCreateNewUser() throws Exception {
        userRepository.save(new User("Dima", "1234"));

        this.mockMvc.perform(
                        post("/register")
                                .param("username", "Dima")
                                .param("password", "1234")
                )
                .andExpect(model().attribute("nameError", "Пользователь с таким именем уже существует"));
    }
}