package com.junnar.tourist_guide.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Only the fields a client may set. id, userId and createdAt are deliberately absent so a
 * client can't overwrite someone else's itinerary by sending their id.
 */
public record ItineraryRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 150, message = "Title must be at most 150 characters")
        String title,

        @Size(max = 2000, message = "Description must be at most 2000 characters")
        String description,

        @Size(max = 50, message = "An itinerary can include at most 50 places")
        List<@NotBlank(message = "Place ids must not be blank") String> placeIds,

        @Min(value = 1, message = "Number of days must be between 1 and 30")
        @Max(value = 30, message = "Number of days must be between 1 and 30")
        int numberOfDays
) {}
