package pl.agh.edu.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.agh.edu.library.model.Category;

public interface CategoryRepository extends JpaRepository<Category,Long>  {

}
