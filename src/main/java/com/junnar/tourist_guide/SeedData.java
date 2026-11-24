package com.junnar.tourist_guide;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;

/** Loads the seed data files in src/main/resources/seed/. Edit those JSON files, not Java. */
final class SeedData {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private SeedData() {}

    static <T> List<T> load(String classpathResource, TypeReference<List<T>> type) {
        try (InputStream in = new ClassPathResource(classpathResource).getInputStream()) {
            return MAPPER.readValue(in, type);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read seed file " + classpathResource, e);
        }
    }
}
