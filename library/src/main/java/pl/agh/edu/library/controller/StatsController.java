package pl.agh.edu.library.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.agh.edu.library.model.*;
import pl.agh.edu.library.service.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final LoanService loanService;
    private final BookService bookService;
    private final ReviewService reviewService;
    private final CategoryService categoryService;

    @Autowired
    public StatsController(LoanService loanService, BookService bookService, ReviewService reviewService, CategoryService categoryService) {
        this.loanService = loanService;
        this.bookService = bookService;
        this.reviewService = reviewService;
        this.categoryService = categoryService;
    }

//    zwraca listę książek, wraz z liczbą ich wypożyczeń
    @GetMapping("/book-loans")
    public List<BookLoanStat> getLoanStats() {
        List<Loan> loans = loanService.getAllLoans();
        Map<Integer, Integer> counts = new HashMap<>();
        Map<Integer, Book> booksById = new HashMap<>();

        for (Loan loan : loans) {
            Book b = loan.getBook();
            if (b == null || b.getId() == null) continue;
            Integer id = b.getId();
            booksById.putIfAbsent(id, b);
            counts.merge(id, 1, Integer::sum);
        }

        List<BookLoanStat> result = new ArrayList<>();
        for (Map.Entry<Integer, Integer> e : counts.entrySet()) {
            Book b = booksById.get(e.getKey());
            if (b == null) continue;
            result.add(new BookLoanStat(b.getId(), b.getName(), b.getAuthor(), e.getValue()));
        }
        return result;
    }

    public record BookLoanStat(Integer bookId, String name, String author, Integer count) {}

    @GetMapping("/user-loans")
    public List<UserLoanStat> getUserStats() {
        List<Loan> loans = loanService.getAllLoans();
        Map<Integer, Integer> counts = new HashMap<>();
        Map<Integer, User> usersById = new HashMap<>();

        for (Loan loan : loans) {
            User u = loan.getUser();
            if (u == null || u.getId() == null) continue;
            Integer id = u.getId();
            usersById.putIfAbsent(id, u);
            counts.merge(id, 1, Integer::sum);
        }

        List<UserLoanStat> result = new ArrayList<>();
        for (Map.Entry<Integer, Integer> e : counts.entrySet()) {
            User u = usersById.get(e.getKey());
            if (u == null) continue;
            result.add(new UserLoanStat(u.getId(), u.getUserName(), u.getEmail(), e.getValue()));
        }
        return result;
    }

    public record UserLoanStat(Integer userId, String userName, String email, Integer count) {}

    @GetMapping("/user-penalties")
    public List<UserPenaltyStat> getUserPenalties() {
        List<Loan> loans = loanService.getAllLoans();
        Map<Integer, Double> penalties = new HashMap<>();
        Map<Integer, User> usersById = new HashMap<>();

        for (Loan loan : loans) {
            User u = loan.getUser();
            if (u == null || u.getId() == null) continue;
            Integer id = u.getId();
            usersById.putIfAbsent(id, u);
            penalties.merge(id, loan.getPenalty(), Double::sum);
        }

        List<UserPenaltyStat> result = new ArrayList<>();
        for (Map.Entry<Integer, Double> e : penalties.entrySet()) {
            User u = usersById.get(e.getKey());
            if (u == null) continue;
            result.add(new UserPenaltyStat(u.getId(), u.getUserName(), u.getEmail(), e.getValue()));
        }
        return result;
    }

    public record UserPenaltyStat(Integer userId, String userName, String email, Double penalty) {}

    @GetMapping("/books-ratings")
    public List<BookRatingStat> getBooksRatings() {
        List<Book> books = bookService.getBooks();
        List<BookRatingStat> result = new ArrayList<>();
        for (Book book : books) {
            if (book == null || book.getId() == null) continue;
            List<Review> reviews = reviewService.getReviewsForBook(book.getId());
            double rating = reviews.isEmpty()
                    ? 0.0
                    : reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
            result.add(new BookRatingStat(book.getId(), book.getName(), book.getAuthor(), rating));
        }
        return result;
    }

    public record BookRatingStat(Integer bookId, String name, String author, Double rating) {}

    @GetMapping("/category-loans")
    public List<CategoryLoanStat> getCategoryStats() {
        List<Book> books = bookService.getBooks();
        Map<Integer, Integer> counts = new HashMap<>();
        Map<Integer, Category> categoriesById = new HashMap<>();

        for (Book book : books) {
            if (book == null || book.getCategories() == null) continue;
            for (Category category : book.getCategories()) {
                if (category == null || category.getId() == null) continue;
                Integer id = category.getId();
                categoriesById.putIfAbsent(id, category);
                counts.merge(id, 1, Integer::sum);
            }
        }

        List<CategoryLoanStat> result = new ArrayList<>();
        for (Map.Entry<Integer, Integer> e : counts.entrySet()) {
            Category c = categoriesById.get(e.getKey());
            if (c == null) continue;
            result.add(new CategoryLoanStat(c.getId(), c.getName(), e.getValue()));
        }
        return result;
    }

    public record CategoryLoanStat(Integer categoryId, String name, Integer count) {}

}
