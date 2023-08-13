package ru.petrov.weathertracker.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UserDTO {
    private String username;
    private String password;
}
