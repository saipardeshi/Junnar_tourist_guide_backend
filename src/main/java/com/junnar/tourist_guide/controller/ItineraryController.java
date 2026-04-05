package com.junnar.tourist_guide.controller;

import com.junnar.tourist_guide.model.Itinerary;
import com.junnar.tourist_guide.repository.UserRepository;
import com.junnar.tourist_guide.service.ItineraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/itineraries")
public class ItineraryController {

    @Autowired private ItineraryService itineraryService;
    @Autowired private UserRepository userRepository;

    // GET /api/itineraries  — returns logged-in user's itineraries
    @GetMapping
    public List<Itinerary> getMyItineraries(Authentication auth) {
        String email = auth.getName();
        String userId = userRepository.findByEmail(email).get().getId();
        return itineraryService.getByUser(userId);
    }

    // POST /api/itineraries
    @PostMapping
    public Itinerary create(@RequestBody Itinerary itinerary, Authentication auth) {
        String email = auth.getName();
        String userId = userRepository.findByEmail(email).get().getId();
        itinerary.setUserId(userId);
        return itineraryService.save(itinerary);
    }

    // DELETE /api/itineraries/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        itineraryService.delete(id);
        return ResponseEntity.ok().build();
    }
}