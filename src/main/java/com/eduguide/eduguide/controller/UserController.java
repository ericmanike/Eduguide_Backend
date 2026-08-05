package com.eduguide.eduguide.controller;

import com.eduguide.eduguide.model.User;
import com.eduguide.eduguide.model.RegisterRequest;
import com.eduguide.eduguide.model.UserRole;
import com.eduguide.eduguide.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.eduguide.eduguide.config.JwtService;
import com.eduguide.eduguide.model.LoginResponse;
import com.eduguide.eduguide.model.LoginRequest;
import java.util.Optional;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {


    private  final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Spring injects both the repository and the password encoder here
    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder,JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // New Registration Route
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            return ResponseEntity.badRequest().body("Error: Email is required!");
        }

        if (request.getName() == null || request.getName().isBlank()) {
            return ResponseEntity.badRequest().body("Error: Name is required!");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body("Error: Password is required!");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: Email is already taken!");
        }

        User newUser = new User();
        newUser.setName(request.getName());
        newUser.setEmail(request.getEmail());
        newUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        newUser.setRole(request.getRole() == null ? UserRole.STUDENT : request.getRole());

        userRepository.save(newUser);

        return ResponseEntity.ok("User registered successfully!");
    }


    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isEmpty() || !passwordEncoder.matches(request.getPassword(), userOptional.get().getPasswordHash())) {
            return ResponseEntity.status(401).body("Error: Invalid email or password!");
        }

        User user = userOptional.get();

        String generatedToken = jwtService.generateToken(user.getEmail());

        return ResponseEntity.ok(new LoginResponse(
                generatedToken,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        ));
    }

}
