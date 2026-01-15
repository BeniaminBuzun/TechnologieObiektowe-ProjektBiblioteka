package pl.agh.edu.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.agh.edu.library.model.Loan;
import pl.agh.edu.library.model.User;

public interface LoanRepository extends JpaRepository<Loan,Long> {
    long countByUserAndState(User user, String state);
    
    //do recezji
    boolean existsByUser_IdAndBook_IdAndState(Integer userId, Integer bookId, String state);
}
