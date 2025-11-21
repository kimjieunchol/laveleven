package com.labelai.repository;

import com.labelai.entity.History;
import com.labelai.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<History, String> {
    
    // 특정 아이템의 이력 조회
    List<History> findByItemOrderByChangedAtDesc(Item item);
    
    // 특정 단계의 이력 조회
    List<History> findByItemAndStepNameOrderByChangedAtDesc(Item item, String stepName);
    
    // ADMIN용: 부서별 이력 조회
    @Query("SELECT h FROM History h WHERE h.item.departmentId = :departmentId " +
           "ORDER BY h.changedAt DESC")
    List<History> findByDepartmentIdOrderByChangedAtDesc(@Param("departmentId") String departmentId);
    
    // USER용: 자기가 생성한 아이템의 이력만 조회
    @Query("SELECT h FROM History h WHERE h.item.createdBy.id = :userId " +
           "ORDER BY h.changedAt DESC")
    List<History> findByCreatedByUserIdOrderByChangedAtDesc(@Param("userId") Long userId);
}