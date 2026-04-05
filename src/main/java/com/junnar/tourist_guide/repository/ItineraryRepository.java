package com.junnar.tourist_guide.repository;

import com.junnar.tourist_guide.model.Itinerary;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ItineraryRepository extends MongoRepository<Itinerary, String> {
    List<Itinerary> findByUserId(String userId);
}