package com.junnar.tourist_guide.service;

import com.junnar.tourist_guide.model.Place;
import com.junnar.tourist_guide.repository.PlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlaceService {

    @Autowired
    private PlaceRepository placeRepository;

    public List<Place> getAllPlaces() {
        return placeRepository.findAll();
    }

    public Optional<Place> getPlaceById(String id) {
        return placeRepository.findById(id);
    }

    public List<Place> getByCategory(String category) {
        return placeRepository.findByCategory(category);
    }

    public List<Place> searchByName(String name) {
        return placeRepository.findByNameContainingIgnoreCase(name);
    }

    public Place savePlace(Place place) {
        return placeRepository.save(place);
    }

    public void deletePlace(String id) {
        placeRepository.deleteById(id);
    }
}