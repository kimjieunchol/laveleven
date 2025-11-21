package com.labelai.repository;

import com.labelai.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email); // Add this method
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}