package com.junnar.tourist_guide.controller;

import com.junnar.tourist_guide.dto.LoginRequest;
import com.junnar.tourist_guide.dto.RegisterRequest;
import com.junnar.tourist_guide.model.User;
import com.junnar.tourist_guide.repository.UserRepository;
import com.junnar.tourist_guide.security.JwtUtil;
import jakarta.validation.Valid;
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
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest body) {
        String email = body.email().trim();
        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already registered"));
        }
        User user = new User(
                body.name().trim(),
                email,
                passwordEncoder.encode(body.password()),
                "USER"
        );
        userRepository.save(user);
        return ResponseEntity.ok(authResponse(user));
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest body) {
        Optional<User> userOpt = userRepository.findByEmail(body.email().trim());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }
        User user = userOpt.get();
        if (!passwordEncoder.matches(body.password(), user.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }
        return ResponseEntity.ok(authResponse(user));
    }

    // The frontend (AuthContext) needs role and id as well as token/name:
    // the admin dashboard guard and the "delete my review" button depend on them.
    private Map<String, String> authResponse(User user) {
        Map<String, String> response = new HashMap<>();
        response.put("token", jwtUtil.generateToken(user.getEmail()));
        response.put("name", user.getName());
        response.put("id", user.getId());
        response.put("role", user.getRole());
        return response;
    }
}