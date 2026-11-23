package com.junnar.tourist_guide.controller;

import com.junnar.tourist_guide.model.Place;
import com.junnar.tourist_guide.service.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/places")
public class PlaceController {

    @Autowired
    private PlaceService placeService;

    // GET /api/places
    @GetMapping
    public List<Place> getAllPlaces() {
        return placeService.getAllPlaces();
    }

    // GET /api/places/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Place> getPlace(@PathVariable String id) {
        return placeService.getPlaceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/places/category/{category}
    @GetMapping("/category/{category}")
    public List<Place> getByCategory(@PathVariable String category) {
        return placeService.getByCategory(category);
    }

    // GET /api/places/search?name=xxx
    @GetMapping("/search")
    public List<Place> search(@RequestParam String name) {
        return placeService.searchByName(name);
    }

    // POST /api/places  (admin only)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Place addPlace(@RequestBody Place place) {
        place.setId(null); // always create a new document; updates go through /api/admin/places/{id}
        return placeService.savePlace(place);
    }

    // DELETE /api/places/{id}  (admin only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePlace(@PathVariable String id) {
        placeService.deletePlace(id);
        return ResponseEntity.ok().build();
    }
}