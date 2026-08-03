package com.eduguide.eduguide.controller;

import com.eduguide.eduguide.model.User;
import com.eduguide.eduguide.model.RegisterRequest;
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
        // 1. Check if username is taken
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }

        // 2. Create new user and hash the password safely
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));

        // 3. Save to Neon Cloud Database
        userRepository.save(newUser);

        return ResponseEntity.ok("User registered successfully!");
    }


    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request) {
        Optional<User> userOptional = userRepository.findByUsername(request.getUsername());

        if (userOptional.isEmpty() || !passwordEncoder.matches(request.getPassword(), userOptional.get().getPassword())) {
            return ResponseEntity.status(401).body("Error: Invalid username or password!");
        }

        User user = userOptional.get();

        // Generate the secure token string for this user
        String generatedToken = jwtService.generateToken(user.getUsername());

        // Return the token wrapped in a clean JSON object structure
        return ResponseEntity.ok(new LoginResponse(generatedToken, user.getUsername()));
    }

}
