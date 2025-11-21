package com.labelai.repository;

import com.labelai.entity.History;
import com.labelai.entity.Item; // New Import
import com.labelai.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID; // New Import

/**
 * 이력 데이터 접근 레포지토리
 */
@Repository
public interface HistoryRepository extends JpaRepository<History, UUID> { // Changed Long to UUID
    List<History> findByChangedBy_UsernameOrderByChangedAtDesc(String username);
    List<History> findByItemOrderByChangedAtDesc(Item item); // New method
}