package com.junnar.tourist_guide.controller;

import com.junnar.tourist_guide.dto.ItineraryRequest;
import com.junnar.tourist_guide.model.Itinerary;
import com.junnar.tourist_guide.repository.UserRepository;
import com.junnar.tourist_guide.service.ItineraryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/itineraries")
public class ItineraryController {

    @Autowired private ItineraryService itineraryService;
    @Autowired private UserRepository userRepository;

    // GET /api/itineraries  — returns logged-in user's itineraries
    @GetMapping
    public List<Itinerary> getMyItineraries(Authentication auth) {
        if (auth == null) return List.of();
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .map(u -> itineraryService.getByUser(u.getId()))
                .orElse(List.of());
    }

    // POST /api/itineraries
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody ItineraryRequest body, Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .map(u -> {
                    // Build the entity server-side: id, userId and createdAt must never come from
                    // the client (a client-supplied id would overwrite someone else's itinerary).
                    Itinerary itinerary = new Itinerary();
                    itinerary.setUserId(u.getId());
                    itinerary.setTitle(body.title().trim());
                    itinerary.setDescription(body.description() == null ? "" : body.description().trim());
                    itinerary.setPlaceIds(body.placeIds() == null ? List.of() : body.placeIds());
                    itinerary.setNumberOfDays(body.numberOfDays());
                    return ResponseEntity.ok(itineraryService.save(itinerary));
                })
                .orElseGet(() -> ResponseEntity.status(401).body((Itinerary) null));
    }

    // DELETE /api/itineraries/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id, Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).build();
        String email = auth.getName();
        userRepository.findByEmail(email).ifPresent(user -> {
            itineraryService.getById(id).ifPresent(it -> {
                if (it.getUserId().equals(user.getId()) || "ADMIN".equals(user.getRole())) {
                    itineraryService.delete(id);
                }
            });
        });
        return ResponseEntity.ok().build();
    }
}