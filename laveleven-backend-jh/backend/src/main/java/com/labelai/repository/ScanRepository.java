package com.labelai.repository;

import com.labelai.entity.Item;
import com.labelai.entity.Scan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ScanRepository extends JpaRepository<Scan, UUID> {
    Optional<Scan> findFirstByItem(Item item);
}
