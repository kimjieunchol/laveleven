package com.labelai.repository;

import com.labelai.entity.SchemaData;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SchemaDataRepository extends JpaRepository<SchemaData, String> {
    Optional<SchemaData> findByItemId(String itemId);
}
