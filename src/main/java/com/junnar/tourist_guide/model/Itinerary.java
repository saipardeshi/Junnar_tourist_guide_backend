package com.junnar.tourist_guide.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "itineraries")
public class Itinerary {

    @Id
    private String id;

    private String userId;       // who created this itinerary
    private String title;
    private String description;
    private List<String> placeIds;  // ordered list of Place IDs
    private int numberOfDays;
    private String createdAt;

    public Itinerary() {}

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getPlaceIds() { return placeIds; }
    public void setPlaceIds(List<String> placeIds) { this.placeIds = placeIds; }

    public int getNumberOfDays() { return numberOfDays; }
    public void setNumberOfDays(int numberOfDays) { this.numberOfDays = numberOfDays; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}