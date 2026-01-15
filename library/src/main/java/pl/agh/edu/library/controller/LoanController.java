package pl.agh.edu.library.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.agh.edu.library.model.Loan;
import pl.agh.edu.library.model.User;
import pl.agh.edu.library.service.LoanService;
import pl.agh.edu.library.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;
    private final UserService userService;

    @Autowired
    public LoanController(LoanService loanService, UserService userService) {
        this.loanService = loanService;
        this.userService = userService;
    }

    private User getLoggedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            System.out.println("LoanController: Authentication is null!");
            throw new RuntimeException("Not authenticated");
        }
        String username = auth.getName();
        System.out.println("LoanController: Getting loans for user: " + username);
        return userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("Logged user not found in DB: " + username));
    }

    @PostMapping("/reserve")
    public ResponseEntity<?> reserveBook(@RequestParam Long bookId) {
        try {
            User user = getLoggedUser();
            Loan loan = loanService.reserveBook(user.getId().longValue(), bookId);
            return ResponseEntity.ok(loan);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/loan")
    public ResponseEntity<?> loanBook(@RequestParam Long bookId) {
        try {
            User user = getLoggedUser();
            Loan loan = loanService.loanBook(user.getId().longValue(), bookId);
            return ResponseEntity.ok(loan);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/extend/{loanId}")
    public ResponseEntity<?> extendLoan(@PathVariable Long loanId) {
        try {
            Loan loan = loanService.extendLoan(loanId);
            return ResponseEntity.ok(loan);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/return/{loanId}")
    public ResponseEntity<Loan> returnBook(@PathVariable Long loanId) {
        try {
            return ResponseEntity.ok(loanService.returnBook(loanId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public List<Loan> getAllLoans() {
        return loanService.getAllLoans();
    }
    
    @GetMapping("/my")
    public ResponseEntity<?> getMyLoans() {
        try {
            User user = getLoggedUser();
            List<Loan> myLoans = loanService.getAllLoans().stream()
                    .filter(loan -> loan.getUser().getId().equals(user.getId()))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(myLoans);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}
