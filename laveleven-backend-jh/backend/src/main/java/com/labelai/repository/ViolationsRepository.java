package com.labelai.repository;

import com.labelai.entity.Violations;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ViolationsRepository extends JpaRepository<Violations, UUID> {
}
