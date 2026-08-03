package com.eduguide.eduguide.repository;



import com.eduguide.eduguide.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // This allows you to look up users by their username for logging in later
    Optional<User> findByUsername(String username);
}

