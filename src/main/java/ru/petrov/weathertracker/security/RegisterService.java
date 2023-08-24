package ru.petrov.weathertracker.security;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.petrov.weathertracker.DTO.UserDTO;
import ru.petrov.weathertracker.models.User;
import ru.petrov.weathertracker.repositories.UserRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
public class RegisterService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void createNewUser(UserDTO newUser) throws UserAlreadyExistsException {
        Optional<User> user = userRepository.findByUsername(newUser.getUsername());
        if (user.isPresent()) {
            throw new UserAlreadyExistsException("Пользователь с таким именем уже существует");
        }

        userRepository.save(new User(
                newUser.getUsername(),
                passwordEncoder.encode(newUser.getPassword())
        ));
    }
}
