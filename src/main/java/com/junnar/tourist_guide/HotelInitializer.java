package com.junnar.tourist_guide;

import com.fasterxml.jackson.core.type.TypeReference;
import com.junnar.tourist_guide.model.Hotel;
import com.junnar.tourist_guide.repository.HotelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(2)   // runs after DataInitializer (Order 1)
public class HotelInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(HotelInitializer.class);
    private static final String HOTELS_RESOURCE = "seed/hotels.json";

    private final HotelRepository hotelRepository;

    public HotelInitializer(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    /** Seeds the hotel catalogue from src/main/resources/seed/hotels.json, only into an empty collection. */
    @Override
    public void run(String... args) {
        if (hotelRepository.count() > 0) {
            log.info("Hotels already seeded, skipping.");
            return;
        }

        List<Hotel> hotels = SeedData.load(HOTELS_RESOURCE, new TypeReference<>() {});
        hotelRepository.saveAll(hotels);
        log.info("{} hotels seeded successfully.", hotels.size());
    }
}
