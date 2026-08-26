package com.junnar.tourist_guide.controller;

import com.junnar.tourist_guide.model.Event;
import com.junnar.tourist_guide.model.Place;
import com.junnar.tourist_guide.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.junnar.tourist_guide.repository.UserRepository;
import com.junnar.tourist_guide.model.User;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired private UserRepository userRepository;
    @Autowired private PlaceRepository placeRepository;
    @Autowired private ReviewRepository reviewRepository;
    @Autowired private ItineraryRepository itineraryRepository;
    @Autowired private EventRepository eventRepository;


    private boolean isAdmin(Authentication auth) {
        if (auth == null) return false;
        User user = userRepository.findByEmail(auth.getName()).orElse(null);
        return user != null && "ADMIN".equals(user.getRole());
    }

    // GET /api/admin/stats
    @GetMapping("/stats")
    public ResponseEntity<?> getStats(Authentication auth) {
        if (!isAdmin(auth)) return ResponseEntity.status(403).body("Forbidden");
        return ResponseEntity.ok(Map.of(
                "totalUsers", userRepository.count(),
                "totalPlaces", placeRepository.count(),
                "totalReviews", reviewRepository.count(),
                "totalItineraries", itineraryRepository.count()
        ));
    }

    // GET /api/admin/users
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(Authentication auth) {
        if (!isAdmin(auth)) return ResponseEntity.status(403).body("Forbidden");
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users.stream().map(u -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", u.getId());
            map.put("name", u.getName());
            map.put("email", u.getEmail());
            map.put("role", u.getRole());
            return map;
        }).toList());
    }

    // DELETE /api/admin/reviews/{id}
    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable String id, Authentication auth) {
        if (!isAdmin(auth)) return ResponseEntity.status(403).body("Forbidden");
        reviewRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Review deleted"));
    }

    // POST /api/admin/places — add place
    @PostMapping("/places")
    public ResponseEntity<?> addPlace(@RequestBody Place place, Authentication auth) {
        if (!isAdmin(auth)) return ResponseEntity.status(403).body("Forbidden");
        return ResponseEntity.ok(placeRepository.save(place));
    }

    // PUT /api/admin/places/{id}
    @PutMapping("/places/{id}")
    public ResponseEntity<?> updatePlace(@PathVariable String id, @RequestBody Place updated, Authentication auth) {
        if (!isAdmin(auth)) return ResponseEntity.status(403).body("Forbidden");
        updated.setId(id);
        return ResponseEntity.ok(placeRepository.save(updated));
    }

    // DELETE /api/admin/places/{id}
    @DeleteMapping("/places/{id}")
    @Transactional
    public ResponseEntity<?> deletePlace(@PathVariable String id, Authentication auth) {
        if (!isAdmin(auth)) return ResponseEntity.status(403).body("Forbidden");
        placeRepository.deleteById(id);
        reviewRepository.deleteByPlaceId(id);
        return ResponseEntity.ok(Map.of("message", "Place deleted"));
    }

    // POST /api/admin/make-admin/{userId}
    @PostMapping("/make-admin/{userId}")
    public ResponseEntity<?> makeAdmin(@PathVariable String userId, Authentication auth) {
        if (!isAdmin(auth)) return ResponseEntity.status(403).body("Forbidden");
        userRepository.findById(userId).ifPresent(u -> {
            u.setRole("ADMIN");
            userRepository.save(u);
        });
        return ResponseEntity.ok(Map.of("message", "User promoted to admin"));
    }

    // POST /api/admin/events
    @PostMapping("/events")
    public ResponseEntity<?> addEvent(@RequestBody Event event, Authentication auth) {
        if (!isAdmin(auth)) return ResponseEntity.status(403).body("Forbidden");
        return ResponseEntity.ok(eventRepository.save(event));
    }

    // DELETE /api/admin/events/{id}
    @DeleteMapping("/events/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable String id, Authentication auth) {
        if (!isAdmin(auth)) return ResponseEntity.status(403).body("Forbidden");
        eventRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Event deleted"));
    }
}