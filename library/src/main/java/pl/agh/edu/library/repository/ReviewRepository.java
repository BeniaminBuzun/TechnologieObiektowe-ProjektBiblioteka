package pl.agh.edu.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.agh.edu.library.model.Review;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByBookId(Integer bookId);
}
