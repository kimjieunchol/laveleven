package com.labelai.repository;

import com.labelai.entity.Scan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ScanRepository extends JpaRepository<Scan, String> {
    List<Scan> findByItemId(String itemId);
}
