package pl.agh.edu.library.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.agh.edu.library.model.*;
import pl.agh.edu.library.service.*;

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
    public Map<Book,Integer> getLoanStats() {
        List<Loan> loans = loanService.getAllLoans();
        Map<Book,Integer> result = new HashMap<>();
        for (Loan loan:loans){
            result.merge(loan.getBook(), 1, Integer::sum);
        }
        return result;
    }
    @GetMapping("/user-loans")
    public Map<User,Integer> getUserStats() {
        List<Loan> loans = loanService.getAllLoans();
        Map<User,Integer> result = new HashMap<>();
        for (Loan loan:loans){
            result.merge(loan.getUser(), 1, Integer::sum);
        }
        return result;
    }
    @GetMapping("/user-penalties")
    public Map<User,Integer> getUserPenalties() {
        List<Loan> loans = loanService.getAllLoans();
        Map<User,Integer> result = new HashMap<>();
        for (Loan loan:loans){
            result.merge(loan.getUser(), 1, Integer::sum);
        }
        return result;
    }
    @GetMapping("/books-ratings")
    public Map<Book,Double> getBooksRatings() {
        List<Book> books = bookService.getBooks();
        Map<Book,Double> result = new HashMap<>();
        for (Book book:books){
            List<Review> reviews= reviewService.getReviewsForBook(book.getId());
            double rating = reviews.stream().mapToInt(Review::getRating).average().getAsDouble();
            result.put(book,rating);
        }
        return result;
    }
    @GetMapping("/category-loans")
    public Map<Category,Integer> getCategoryStats() {
        List<Book> books = bookService.getBooks();
        Map<Category,Integer> result = new HashMap<>();
        for (Book book:books){
            for (Category category:book.getCategories()){
                result.merge(category, 1, Integer::sum);
            }
        }
        return result;
    }

}
