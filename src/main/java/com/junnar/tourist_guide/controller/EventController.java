package com.junnar.tourist_guide.controller;

import com.junnar.tourist_guide.model.Event;
import com.junnar.tourist_guide.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    // GET /api/events -- all events
    @GetMapping
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    // GET /api/events/month/{month}
    @GetMapping("/month/{month}")
    public List<Event> getByMonth(@PathVariable String month) {
        return eventRepository.findByMonth(month);
    }

    // GET /api/events/tag/{tag}
    @GetMapping("/tag/{tag}")
    public List<Event> getByTag(@PathVariable String tag) {
        return eventRepository.findByTag(tag);
    }

    // GET /api/events/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Event> getById(@PathVariable String id) {
        return eventRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
