package com.labelai.repository;

import com.labelai.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    
    // ADMIN용: 같은 부서 사용자 조회
    List<User> findByDepartmentId(String departmentId);
    
    // 활성 사용자만 조회
    List<User> findByIsActiveTrue();
}