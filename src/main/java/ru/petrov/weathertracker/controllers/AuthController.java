package ru.petrov.weathertracker.controllers;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.petrov.weathertracker.DTO.UserDTO;
import ru.petrov.weathertracker.models.User;
import ru.petrov.weathertracker.security.RegisterService;
import ru.petrov.weathertracker.security.UserAlreadyExistsException;

@Controller
@AllArgsConstructor
public class AuthController {

    private final RegisterService registerService;


    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String addUser(UserDTO newUser, Model model) {
        try {
            registerService.createNewUser(newUser);
        } catch (UserAlreadyExistsException e) {
            model.addAttribute("nameError", e.getMessage());

            return "register";
        }

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }


}
