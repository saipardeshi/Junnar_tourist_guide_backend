package com.junnar.tourist_guide.controller;

import com.junnar.tourist_guide.dto.UpdateProfileRequest;
import com.junnar.tourist_guide.model.User;
import com.junnar.tourist_guide.repository.ItineraryRepository;
import com.junnar.tourist_guide.repository.ReviewRepository;
import com.junnar.tourist_guide.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired private UserRepository userRepository;
    @Autowired private ItineraryRepository itineraryRepository;
    @Autowired private ReviewRepository reviewRepository;

    // GET /api/user/profile
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        int itineraryCount = itineraryRepository.findByUserId(user.getId()).size();
        int reviewCount = reviewRepository.findByUserId(user.getId()).size();
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("role", user.getRole());
        response.put("favourites", user.getFavourites() != null ? user.getFavourites() : new ArrayList<>());
        response.put("itineraryCount", itineraryCount);
        response.put("reviewCount", reviewCount);
        
        return ResponseEntity.ok(response);
    }

    // PUT /api/user/profile — update name
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody UpdateProfileRequest body, Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        user.setName(body.name().trim());
        userRepository.save(user);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Profile updated");
        response.put("name", user.getName());
        return ResponseEntity.ok(response);
    }

    // POST /api/user/favourites/{placeId} — toggle favourite
    @PostMapping("/favourites/{placeId}")
    public ResponseEntity<?> toggleFavourite(@PathVariable String placeId, Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        if (user.getFavourites() == null) {
            user.setFavourites(new ArrayList<>());
        }
        if (user.getFavourites().contains(placeId)) {
            user.getFavourites().remove(placeId);
        } else {
            user.getFavourites().add(placeId);
        }
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("favourites", user.getFavourites()));
    }

    // GET /api/user/favourites
    @GetMapping("/favourites")
    public ResponseEntity<?> getFavourites(Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        return ResponseEntity.ok(Map.of("favourites", user.getFavourites() != null ? user.getFavourites() : new ArrayList<>()));
    }
}