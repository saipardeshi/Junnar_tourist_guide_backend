package com.junnar.tourist_guide.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Proxies OpenWeatherMap so the API key stays on the server instead of being shipped to every
 * visitor inside the JavaScript bundle. Responses are cached so the free-tier quota isn't burned.
 */
@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    private static final Logger log = LoggerFactory.getLogger(WeatherController.class);
    private static final Duration CACHE_TTL = Duration.ofMinutes(10);
    private static final String URL =
            "https://api.openweathermap.org/data/2.5/weather?q=Junnar,IN&units=metric&appid={key}";

    /** Only what the weather widget needs - not the whole upstream payload. */
    public record Weather(Main main, List<Condition> weather) {
        public record Main(double temp, int humidity) {}
        public record Condition(String main, String description) {}
    }

    private final RestClient restClient;
    private final String apiKey;

    private Weather cached;
    private Instant cachedAt = Instant.MIN;

    public WeatherController(RestClient.Builder builder, @Value("${weather.api-key:}") String apiKey) {
        this.restClient = builder.build();
        this.apiKey = apiKey == null ? "" : apiKey.trim();
    }

    // GET /api/weather — current weather in Junnar
    @GetMapping
    public synchronized ResponseEntity<?> current() {
        if (apiKey.isEmpty()) {
            return ResponseEntity.status(503).body(Map.of("error", "Weather is not configured"));
        }
        if (cached != null && Instant.now().isBefore(cachedAt.plus(CACHE_TTL))) {
            return ResponseEntity.ok(cached);
        }
        try {
            JsonNode body = restClient.get().uri(URL, apiKey).retrieve().body(JsonNode.class);
            Weather fresh = parse(body);
            if (fresh != null) {
                cached = fresh;
                cachedAt = Instant.now();
                return ResponseEntity.ok(fresh);
            }
            log.warn("Weather provider returned an unexpected payload");
        } catch (RestClientException e) {
            // Log only the exception type: some messages embed the request URL, which contains the key.
            log.warn("Weather provider request failed: {}", e.getClass().getSimpleName());
        }
        // Prefer slightly stale data over an error.
        if (cached != null) return ResponseEntity.ok(cached);
        return ResponseEntity.status(502).body(Map.of("error", "Weather is unavailable right now"));
    }

    private static Weather parse(JsonNode body) {
        if (body == null) return null;
        JsonNode main = body.path("main");
        JsonNode first = body.path("weather").path(0);
        if (!main.has("temp") || first.isMissingNode()) return null;
        return new Weather(
                new Weather.Main(main.path("temp").asDouble(), main.path("humidity").asInt()),
                List.of(new Weather.Condition(first.path("main").asText(""), first.path("description").asText(""))));
    }
}
