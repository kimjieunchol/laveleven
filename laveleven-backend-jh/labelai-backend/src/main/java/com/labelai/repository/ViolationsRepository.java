package com.labelai.repository;

import com.labelai.entity.Violations;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ViolationsRepository extends JpaRepository<Violations, String> {
    Optional<Violations> findByItemId(String itemId);
}
