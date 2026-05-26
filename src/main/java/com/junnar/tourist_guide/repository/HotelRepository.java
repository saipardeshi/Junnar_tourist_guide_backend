package com.junnar.tourist_guide.repository;

import com.junnar.tourist_guide.model.Hotel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends MongoRepository<Hotel, String> {
    List<Hotel> findByType(String type);
    List<Hotel> findByAvailableTrue();
}