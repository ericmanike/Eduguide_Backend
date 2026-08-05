package com.eduguide.eduguide.controller;

import com.eduguide.eduguide.model.User;
import com.eduguide.eduguide.model.RegisterRequest;
import com.eduguide.eduguide.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.eduguide.eduguide.config.JwtService;
import com.eduguide.eduguide.model.LoginResponse;
import com.eduguide.eduguide.model.LoginRequest;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {


    private final JwtService jwtService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    // Spring injects both the repository and the password encoder here
    public UserController(UserService userService, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable UUID id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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

        if (userService.getUserByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: Email is already taken!");
        }

        userService.registerUser(request);

        return ResponseEntity.ok("User registered successfully!");
    }


    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request) {
        Optional<User> userOptional = userService.getUserByEmail(request.getEmail());

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

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable UUID id, @RequestBody RegisterRequest request) {
        return userService.updateUser(id, request)
                .map(user -> ResponseEntity.ok("User updated successfully!"))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
        if (userService.deleteUser(id)) {
            return ResponseEntity.ok("User deleted successfully!");
        }
        return ResponseEntity.notFound().build();
    }

}
