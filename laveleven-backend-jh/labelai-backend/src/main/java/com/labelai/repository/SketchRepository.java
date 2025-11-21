package com.labelai.repository;

import com.labelai.entity.Sketch;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SketchRepository extends JpaRepository<Sketch, String> {
    Optional<Sketch> findByItemId(String itemId);
}
