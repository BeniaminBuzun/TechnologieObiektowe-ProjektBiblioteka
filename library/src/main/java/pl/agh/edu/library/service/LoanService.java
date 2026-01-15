package pl.agh.edu.library.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.agh.edu.library.model.Book;
import pl.agh.edu.library.model.Loan;
import pl.agh.edu.library.model.User;
import pl.agh.edu.library.repository.BookRepository;
import pl.agh.edu.library.repository.LoanRepository;
import pl.agh.edu.library.repository.UserRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Autowired
    public LoanService(LoanRepository loanRepository, UserRepository userRepository, BookRepository bookRepository) {
        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    public Loan reserveBook(Long userId, Long bookId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));

        Loan loan = new Loan();
        loan.setUser(user);
        loan.setBook(book);
        loan.setState("RESERVED");
        loan.setReservationDate(Date.valueOf(LocalDate.now()));
        
        return loanRepository.save(loan);
    }

    public Loan loanBook(Long userId, Long bookId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        
        long activeLoansCount = user.getLoans().stream()
                .filter(l -> "LOANED".equals(l.getState()))
                .count();

        if (activeLoansCount >= 10) {
            throw new RuntimeException("Osiągnięto limit 10 wypożyczonych książek!");
        }

        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));

        Loan loan = new Loan();
        loan.setUser(user);
        loan.setBook(book);
        loan.setState("LOANED");
        loan.setLoanDate(Date.valueOf(LocalDate.now()));
        loan.setDueDate(Date.valueOf(LocalDate.now().plusDays(14))); // 14 dni na zwrot
        
        return loanRepository.save(loan);
    }
    
    public Loan extendLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new RuntimeException("Loan not found"));
        
        if (!"LOANED".equals(loan.getState())) {
            throw new RuntimeException("Można przedłużyć tylko aktywne wypożyczenie.");
        }
        
        if (loan.isExtended()) {
            throw new RuntimeException("To wypożyczenie było już przedłużane.");
        }
        
        // Przedłużamy o kolejne 14 dni od obecnego terminu
        LocalDate newDueDate = loan.getDueDate().toLocalDate().plusDays(14);
        loan.setDueDate(Date.valueOf(newDueDate));
        loan.setExtended(true);
        
        return loanRepository.save(loan);
    }
    
    public Loan returnBook(Long loanId) {
        Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new RuntimeException("Loan not found"));
        loan.setState("RETURNED");
        loan.setReturnDate(Date.valueOf(LocalDate.now()));
        return loanRepository.save(loan);
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }
}
