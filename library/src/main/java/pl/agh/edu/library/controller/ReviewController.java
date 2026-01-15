package pl.agh.edu.library.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.agh.edu.library.model.Review;
import pl.agh.edu.library.model.User;
import pl.agh.edu.library.service.ReviewService;
import pl.agh.edu.library.service.UserService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;

    @Autowired
    public ReviewController(ReviewService reviewService, UserService userService) {
        this.reviewService = reviewService;
        this.userService = userService;
    }

    private User getLoggedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("Logged user not found"));
    }

    @PostMapping
    public ResponseEntity<?> addReview(@RequestBody Map<String, Object> payload) {
        try {
            User user = getLoggedUser();
            Long userId = user.getId().longValue();
            
            Long bookId = Long.valueOf(payload.get("bookId").toString());
            Integer rating = Integer.valueOf(payload.get("rating").toString());
            String comment = (String) payload.get("comment");

            Review review = reviewService.addReview(userId, bookId, rating, comment);
            return ResponseEntity.ok(review);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Błąd serwera: " + e.getMessage());
        }
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<Review>> getReviews(@PathVariable Long bookId) {
        return ResponseEntity.ok(reviewService.getReviewsForBook(bookId));
    }
}
