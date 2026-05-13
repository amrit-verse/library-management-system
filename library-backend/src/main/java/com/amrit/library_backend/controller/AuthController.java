package com.amrit.library_backend.controller;

import com.amrit.library_backend.entity.User;
import com.amrit.library_backend.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public String registerUser(@RequestBody User user) {

        if(userRepository.findByEmail(user.getEmail()) != null) {
            return "Email already exists!";
        }

        userRepository.save(user);

        return "User registered successfully!";
    }
}
