package com.junnar.tourist_guide.repository;

import com.junnar.tourist_guide.model.Place;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface PlaceRepository extends MongoRepository<Place, String> {
    List<Place> findByCategory(String category);
    List<Place> findByNameContainingIgnoreCase(String name);
    List<Place> findByTagsContaining(String tag);
    boolean existsByName(String name);
}