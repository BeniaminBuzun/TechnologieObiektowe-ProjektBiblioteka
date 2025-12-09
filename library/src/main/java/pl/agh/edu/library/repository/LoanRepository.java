package pl.agh.edu.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.agh.edu.library.model.Loan;

public interface LoanRepository extends JpaRepository<Loan,Long> {
}
