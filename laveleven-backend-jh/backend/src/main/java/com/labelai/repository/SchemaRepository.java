package com.labelai.repository;

import com.labelai.entity.Item;
import com.labelai.entity.Schema;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SchemaRepository extends JpaRepository<Schema, UUID> {
    Optional<Schema> findFirstByItem(Item item);
}
