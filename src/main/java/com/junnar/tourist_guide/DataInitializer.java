package com.junnar.tourist_guide;

import com.fasterxml.jackson.core.type.TypeReference;
import com.junnar.tourist_guide.model.Place;
import com.junnar.tourist_guide.model.User;
import com.junnar.tourist_guide.repository.PlaceRepository;
import com.junnar.tourist_guide.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private static final String ADMIN_EMAIL = "admin@junnar.com";
    /** The password this project used to hard-code; only used to warn if it is still active. */
    private static final String LEGACY_DEFAULT_ADMIN_PASSWORD = "admin@123";
    private static final String PLACES_RESOURCE = "seed/places.json";

    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminPassword;

    public DataInitializer(PlaceRepository placeRepository,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           @Value("${app.admin.password:}") String adminPassword) {
        this.placeRepository = placeRepository;
        this.userRepository  = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminPassword   = adminPassword;
    }

    @Override
    public void run(String... args) {
        seedAdmin();
        seedPlaces();
    }

    // ── Admin user ────────────────────────────────────────────────────────
    // The admin password comes from the ADMIN_PASSWORD environment variable
    // (app.admin.password). It is never hard-coded or logged. Setting it also
    // lets you rotate the password: change the variable and restart.
    private void seedAdmin() {
        boolean haveConfiguredPassword = adminPassword != null && !adminPassword.isBlank();
        User admin = userRepository.findByEmail(ADMIN_EMAIL).orElse(null);

        if (admin == null) {
            if (!haveConfiguredPassword) {
                log.warn("No admin user exists and ADMIN_PASSWORD is not set - skipping admin creation.");
                return;
            }
            userRepository.save(new User("Admin", ADMIN_EMAIL, passwordEncoder.encode(adminPassword), "ADMIN"));
            log.info("Admin user created ({})", ADMIN_EMAIL);
            return;
        }

        if (haveConfiguredPassword) {
            if (!passwordEncoder.matches(adminPassword, admin.getPassword())) {
                admin.setPassword(passwordEncoder.encode(adminPassword));
                userRepository.save(admin);
                log.info("Admin password updated from ADMIN_PASSWORD.");
            }
        } else if (passwordEncoder.matches(LEGACY_DEFAULT_ADMIN_PASSWORD, admin.getPassword())) {
            log.warn("SECURITY: the admin account still uses the old default password. "
                    + "Set ADMIN_PASSWORD and restart to change it.");
        }
    }

    // ── Places ────────────────────────────────────────────────────────────
    // The place catalogue lives in src/main/resources/seed/places.json.
    // Seed only the places that are missing (matched by name). Existing places
    // are left untouched so their IDs stay stable - favourites, itineraries,
    // reviews and sitemap URLs all reference those IDs - and places added or
    // edited through the admin panel survive restarts.
    private void seedPlaces() {
        List<Place> places = SeedData.load(PLACES_RESOURCE, new TypeReference<>() {});

        List<Place> missing = places.stream()
                .filter(p -> !placeRepository.existsByName(p.getName()))
                .toList();
        if (missing.isEmpty()) {
            log.info("Places already seeded ({} known), nothing to add.", places.size());
            return;
        }
        placeRepository.saveAll(missing);
        log.info("Seeded {} new place(s).", missing.size());
    }
}
