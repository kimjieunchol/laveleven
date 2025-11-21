package com.labelai.repository;

import com.labelai.entity.Item;
import com.labelai.entity.Sketch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SketchRepository extends JpaRepository<Sketch, UUID> {
    Optional<Sketch> findFirstByItem(Item item);
}
