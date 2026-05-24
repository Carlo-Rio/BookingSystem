package repository;

import entity.User;
import entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    User findByStatus(UserStatus status);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
