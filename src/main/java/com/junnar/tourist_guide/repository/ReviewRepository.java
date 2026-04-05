package com.junnar.tourist_guide.repository;

import com.junnar.tourist_guide.model.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ReviewRepository extends MongoRepository<Review, String> {
    List<Review> findByPlaceId(String placeId);
    List<Review> findByUserId(String userId);
    void deleteByPlaceId(String placeId);
}