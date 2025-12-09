package pl.agh.edu.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.agh.edu.library.model.Book;

public interface BookRepository extends JpaRepository<Book,Long> {
}
