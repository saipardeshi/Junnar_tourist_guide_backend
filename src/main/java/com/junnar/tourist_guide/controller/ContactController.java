package com.junnar.tourist_guide.controller;

import com.junnar.tourist_guide.model.ContactMessage;
import com.junnar.tourist_guide.repository.ContactMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    @Autowired private ContactMessageRepository contactMessageRepository;

    // POST /api/contact — public "Contact us" form
    @PostMapping
    public ResponseEntity<?> submit(@RequestBody Map<String, String> body) {
        String name    = trim(body.get("name"));
        String email   = trim(body.get("email"));
        String subject = trim(body.get("subject"));
        String message = trim(body.get("message"));

        if (name.isEmpty() || email.isEmpty() || subject.isEmpty() || message.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Name, email, subject and message are required"));
        }
        if (!EMAIL.matcher(email).matches()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Enter a valid email"));
        }
        // This endpoint is public, so cap sizes to keep it from being used to fill the database.
        if (name.length() > 100 || email.length() > 254 || subject.length() > 200 || message.length() > 5000) {
            return ResponseEntity.badRequest().body(Map.of("error", "One or more fields are too long"));
        }

        ContactMessage entry = new ContactMessage();
        entry.setName(name);
        entry.setEmail(email);
        entry.setSubject(subject);
        entry.setMessage(message);
        entry.setCreatedAt(LocalDateTime.now().toString());
        contactMessageRepository.save(entry);

        return ResponseEntity.ok(Map.of("message", "Message received"));
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }
}
