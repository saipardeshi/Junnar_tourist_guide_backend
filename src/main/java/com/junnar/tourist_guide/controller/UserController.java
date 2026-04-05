package com.junnar.tourist_guide.controller;

import com.junnar.tourist_guide.model.User;
import com.junnar.tourist_guide.repository.ItineraryRepository;
import com.junnar.tourist_guide.repository.ReviewRepository;
import com.junnar.tourist_guide.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired private UserRepository userRepository;
    @Autowired private ItineraryRepository itineraryRepository;
    @Autowired private ReviewRepository reviewRepository;

    // GET /api/user/profile
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication auth) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        int itineraryCount = itineraryRepository.findByUserId(user.getId()).size();
        int reviewCount = reviewRepository.findByUserId(user.getId()).size();
        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "role", user.getRole(),
                "favourites", user.getFavourites(),
                "itineraryCount", itineraryCount,
                "reviewCount", reviewCount
        ));
    }

    // PUT /api/user/profile — update name
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, String> body, Authentication auth) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        if (body.containsKey("name")) user.setName(body.get("name"));
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Profile updated", "name", user.getName()));
    }

    // POST /api/user/favourites/{placeId} — toggle favourite
    @PostMapping("/favourites/{placeId}")
    public ResponseEntity<?> toggleFavourite(@PathVariable String placeId, Authentication auth) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
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
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        return ResponseEntity.ok(Map.of("favourites", user.getFavourites()));
    }
}