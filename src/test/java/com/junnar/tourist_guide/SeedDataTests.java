package com.junnar.tourist_guide;

import com.fasterxml.jackson.core.type.TypeReference;
import com.junnar.tourist_guide.model.Hotel;
import com.junnar.tourist_guide.model.Place;
import com.junnar.tourist_guide.repository.HotelRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** Guards the JSON seed files against typos and accidental edits. No Spring context, no database. */
class SeedDataTests {

    @Test
    void placesJsonLoadsAndIsWellFormed() {
        List<Place> places = SeedData.load("seed/places.json", new TypeReference<>() {});

        assertEquals(29, places.size());
        Set<String> names = places.stream().map(Place::getName).collect(Collectors.toSet());
        assertEquals(places.size(), names.size(), "place names must be unique (they are the seed key)");
        for (Place p : places) {
            assertFalse(p.getName() == null || p.getName().isBlank(), "name");
            assertFalse(p.getCategory() == null || p.getCategory().isBlank(), "category of " + p.getName());
            assertFalse(p.getDescription() == null || p.getDescription().isBlank(), "description of " + p.getName());
            assertTrue(p.getRating() >= 0 && p.getRating() <= 5, "rating of " + p.getName());
        }
        assertTrue(names.contains("Shivneri Fort"));
        assertTrue(names.contains("Ambe Hatvij"));
    }

    @Test
    void hotelsJsonLoadsAndIsWellFormed() {
        List<Hotel> hotels = SeedData.load("seed/hotels.json", new TypeReference<>() {});

        assertEquals(7, hotels.size());
        for (Hotel h : hotels) {
            assertFalse(h.getName() == null || h.getName().isBlank(), "name");
            assertTrue(h.isAvailable(), "available: " + h.getName());
            assertNotNull(h.getRoomTypes(), "rooms of " + h.getName());
            assertNotNull(h.getMeals(), "meals of " + h.getName());
        }
    }

    @Test
    void hotelsAreSeededOnlyIntoAnEmptyCollection() {
        HotelRepository repo = mock(HotelRepository.class);

        when(repo.count()).thenReturn(3L);
        new HotelInitializer(repo).run();
        verify(repo, never()).saveAll(anyIterable());

        when(repo.count()).thenReturn(0L);
        new HotelInitializer(repo).run();
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Hotel>> saved = ArgumentCaptor.forClass(List.class);
        verify(repo).saveAll(saved.capture());
        assertEquals(7, saved.getValue().size());
    }

    @Test
    void missingSeedFileFailsLoudly() {
        assertThrows(java.io.UncheckedIOException.class,
                () -> SeedData.load("seed/does-not-exist.json", new TypeReference<List<Place>>() {}));
    }
}
