package com.junnar.tourist_guide.controller;

import com.junnar.tourist_guide.dto.ReviewRequest;
import com.junnar.tourist_guide.model.Review;
import com.junnar.tourist_guide.model.User;
import com.junnar.tourist_guide.repository.ReviewRepository;
import com.junnar.tourist_guide.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired private ReviewRepository reviewRepository;
    @Autowired private UserRepository userRepository;

    // GET /api/reviews/place/{placeId}
    @GetMapping("/place/{placeId}")
    public List<Review> getReviewsForPlace(@PathVariable String placeId) {
        return reviewRepository.findByPlaceId(placeId);
    }

    // POST /api/reviews
    @PostMapping
    public ResponseEntity<?> addReview(@Valid @RequestBody ReviewRequest body, Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow();

        Review review = new Review();
        review.setPlaceId(body.placeId().trim());
        review.setUserId(user.getId());
        review.setUserName(user.getName());
        review.setRating(body.rating());
        review.setComment(body.comment() == null ? "" : body.comment().trim());
        review.setCreatedAt(LocalDateTime.now().toString());

        return ResponseEntity.ok(reviewRepository.save(review));
    }

    // DELETE /api/reviews/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable String id, Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).build();
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        reviewRepository.findById(id).ifPresent(r -> {
            if (user.getId().equals(r.getUserId()) || "ADMIN".equals(user.getRole())) {
                reviewRepository.deleteById(id);
            }
        });
        return ResponseEntity.ok().build();
    }

    // GET /api/reviews/stats/{placeId} — average rating + count
    @GetMapping("/stats/{placeId}")
    public ResponseEntity<?> getStats(@PathVariable String placeId) {
        List<Review> reviews = reviewRepository.findByPlaceId(placeId);
        double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0);
        return ResponseEntity.ok(Map.of(
                "count", reviews.size(),
                "average", Math.round(avg * 10.0) / 10.0
        ));
    }
}