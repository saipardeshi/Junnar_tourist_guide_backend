package com.junnar.tourist_guide;

import com.junnar.tourist_guide.model.Place;
import com.junnar.tourist_guide.model.User;
import com.junnar.tourist_guide.repository.PlaceRepository;
import com.junnar.tourist_guide.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/** Plain unit tests (no Spring context, no database). */
class DataInitializerTests {

    private PlaceRepository placeRepository;
    private UserRepository userRepository;
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        placeRepository = mock(PlaceRepository.class);
        userRepository = mock(UserRepository.class);
    }

    private DataInitializer initializer(String adminPassword) {
        return new DataInitializer(placeRepository, userRepository, encoder, adminPassword);
    }

    @Test
    void startupNeverWipesExistingPlaces() {
        when(placeRepository.existsByName(anyString())).thenReturn(true); // everything already seeded

        initializer("").run();

        verify(placeRepository, never()).deleteAll();
        verify(placeRepository, never()).deleteById(any());
        verify(placeRepository, never()).saveAll(anyIterable()); // nothing to add -> existing IDs untouched
    }

    @Test
    void onlyMissingPlacesAreInserted() {
        when(placeRepository.existsByName(anyString())).thenReturn(true);
        when(placeRepository.existsByName("Ambe Hatvij")).thenReturn(false); // pretend this one is new

        initializer("").run();

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Place>> captor = ArgumentCaptor.forClass(List.class);
        verify(placeRepository).saveAll(captor.capture());
        assertEquals(1, captor.getValue().size());
        assertEquals("Ambe Hatvij", captor.getValue().get(0).getName());
        verify(placeRepository, never()).deleteAll();
    }

    @Test
    void adminIsNotCreatedWithoutConfiguredPassword() {
        when(placeRepository.existsByName(anyString())).thenReturn(true);
        when(userRepository.findByEmail("admin@junnar.com")).thenReturn(Optional.empty());

        initializer("").run();

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void adminIsCreatedWithConfiguredPassword() {
        when(placeRepository.existsByName(anyString())).thenReturn(true);
        when(userRepository.findByEmail("admin@junnar.com")).thenReturn(Optional.empty());

        initializer("s3cret-pass").run();

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("ADMIN", captor.getValue().getRole());
        assertTrue(encoder.matches("s3cret-pass", captor.getValue().getPassword()));
    }

    @Test
    void existingAdminPasswordIsRotatedWhenConfiguredPasswordDiffers() {
        when(placeRepository.existsByName(anyString())).thenReturn(true);
        User admin = new User("Admin", "admin@junnar.com", encoder.encode("admin@123"), "ADMIN");
        when(userRepository.findByEmail("admin@junnar.com")).thenReturn(Optional.of(admin));

        initializer("brand-new-pass").run();

        verify(userRepository).save(admin);
        assertTrue(encoder.matches("brand-new-pass", admin.getPassword()));
        assertFalse(encoder.matches("admin@123", admin.getPassword()));
    }

    @Test
    void existingAdminIsLeftAloneWhenPasswordAlreadyMatches() {
        when(placeRepository.existsByName(anyString())).thenReturn(true);
        User admin = new User("Admin", "admin@junnar.com", encoder.encode("same-pass"), "ADMIN");
        when(userRepository.findByEmail("admin@junnar.com")).thenReturn(Optional.of(admin));

        initializer("same-pass").run();

        verify(userRepository, never()).save(any(User.class));
    }
}
