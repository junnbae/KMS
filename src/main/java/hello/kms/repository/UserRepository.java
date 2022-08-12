package hello.kms.repository;

import hello.kms.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User save(User member);
    Optional<User> findByUserId(String userId);
//    Optional<User> findAll();
    Page<User> findByRoles(String role, Pageable pageable);
}
