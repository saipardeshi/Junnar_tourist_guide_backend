package com.junnar.tourist_guide.controller;

import com.junnar.tourist_guide.model.User;
import com.junnar.tourist_guide.repository.UserRepository;
import com.junnar.tourist_guide.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private UserRepository userRepository;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private PasswordEncoder passwordEncoder;

    // POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email and password are required"));
        }
        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already registered"));
        }
        User user = new User(
                body.get("name"),
                email,
                passwordEncoder.encode(password),
                "USER"
        );
        userRepository.save(user);
        String token = jwtUtil.generateToken(email);
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("name", user.getName());
        return ResponseEntity.ok(response);
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email and password are required"));
        }
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }
        User user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }
        String token = jwtUtil.generateToken(email);
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("name", user.getName());
        return ResponseEntity.ok(response);
    }
}