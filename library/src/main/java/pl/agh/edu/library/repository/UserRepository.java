package pl.agh.edu.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.agh.edu.library.model.User;

public interface UserRepository extends JpaRepository<User,Long> {
    User findByUserName(String userName);
    boolean existsByUserName(String userName);
}
