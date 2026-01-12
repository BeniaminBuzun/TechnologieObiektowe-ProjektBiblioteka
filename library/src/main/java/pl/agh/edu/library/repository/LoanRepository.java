package pl.agh.edu.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.agh.edu.library.model.Loan;
import pl.agh.edu.library.model.User;

public interface LoanRepository extends JpaRepository<Loan,Long> {
    long countByUserAndState(User user, String state);
}
