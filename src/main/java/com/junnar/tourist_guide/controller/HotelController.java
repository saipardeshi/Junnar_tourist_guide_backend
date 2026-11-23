package com.junnar.tourist_guide.controller;

import com.junnar.tourist_guide.model.Hotel;
import com.junnar.tourist_guide.repository.HotelRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {   // CORS is configured once, globally, in SecurityConfig

    private final HotelRepository hotelRepository;

    public HotelController(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    // GET all hotels
    @GetMapping
    public ResponseEntity<List<Hotel>> getAllHotels() {
        return ResponseEntity.ok(hotelRepository.findAll());
    }

    // GET hotel by ID
    @GetMapping("/{id}")
    public ResponseEntity<Hotel> getHotelById(@PathVariable String id) {
        return hotelRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET hotels by type (Hotel, Lodge, Resort, Homestay)
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Hotel>> getByType(@PathVariable String type) {
        return ResponseEntity.ok(hotelRepository.findByType(type));
    }

    // POST — Admin only
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Hotel> createHotel(@RequestBody Hotel hotel) {
        hotel.setAvailable(true);
        return ResponseEntity.ok(hotelRepository.save(hotel));
    }

    // PUT — Admin only
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Hotel> updateHotel(@PathVariable String id, @RequestBody Hotel hotel) {
        return hotelRepository.findById(id).map(existing -> {
            hotel.setId(id);
            return ResponseEntity.ok(hotelRepository.save(hotel));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE — Admin only
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteHotel(@PathVariable String id) {
        hotelRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}