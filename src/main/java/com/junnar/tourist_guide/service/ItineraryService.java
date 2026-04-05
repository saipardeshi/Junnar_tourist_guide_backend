package com.junnar.tourist_guide.service;

import com.junnar.tourist_guide.model.Itinerary;
import com.junnar.tourist_guide.repository.ItineraryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ItineraryService {

    @Autowired
    private ItineraryRepository itineraryRepository;

    public List<Itinerary> getByUser(String userId) {
        return itineraryRepository.findByUserId(userId);
    }

    public Optional<Itinerary> getById(String id) {
        return itineraryRepository.findById(id);
    }

    public Itinerary save(Itinerary itinerary) {
        if (itinerary.getCreatedAt() == null) {
            itinerary.setCreatedAt(LocalDateTime.now().toString());
        }
        return itineraryRepository.save(itinerary);
    }

    public void delete(String id) {
        itineraryRepository.deleteById(id);
    }
}