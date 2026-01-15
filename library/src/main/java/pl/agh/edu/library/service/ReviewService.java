package pl.agh.edu.library.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.agh.edu.library.model.Book;
import pl.agh.edu.library.model.Review;
import pl.agh.edu.library.model.User;
import pl.agh.edu.library.repository.BookRepository;
import pl.agh.edu.library.repository.LoanRepository;
import pl.agh.edu.library.repository.ReviewRepository;
import pl.agh.edu.library.repository.UserRepository;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository, LoanRepository loanRepository, UserRepository userRepository, BookRepository bookRepository) {
        this.reviewRepository = reviewRepository;
        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    public Review addReview(Long userId, Long bookId, Integer rating, String comment) {
        // 1. Sprawdź czy użytkownik i książka istnieją
        // Uwaga: Rzutowanie Long na Integer, bo w modelach masz Integer, a w kontrolerach Long
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));

        // 2. Sprawdź czy użytkownik kiedykolwiek oddał tę książkę
        boolean hasReturnedBook = loanRepository.existsByUser_IdAndBook_IdAndState(user.getId(), book.getId(), "RETURNED");

        if (!hasReturnedBook) {
            throw new RuntimeException("Nie możesz ocenić książki, której nie przeczytałeś (nie oddałeś)!");
        }

        // 3. Dodaj recenzję
        Review review = new Review(rating, comment, user, book);
        return reviewRepository.save(review);
    }

    public List<Review> getReviewsForBook(Long bookId) {
        // Rzutowanie Long na Integer
        return reviewRepository.findByBookId(bookId.intValue());
    }
}
