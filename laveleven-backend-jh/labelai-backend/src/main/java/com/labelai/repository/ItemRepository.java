package com.labelai.repository;

import com.labelai.entity.Item;
import com.labelai.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, String> {
    
    // USER용: 자기가 생성한 아이템만 조회
    List<Item> findByCreatedBy(User createdBy);
    
    // ADMIN용: 같은 부서 아이템 조회
    List<Item> findByDepartmentId(String departmentId);
    
    // 검색 기능
    @Query("SELECT i FROM Item i WHERE " +
           "i.itemName LIKE %:keyword% OR " +
           "i.description LIKE %:keyword%")
    List<Item> searchByKeyword(@Param("keyword") String keyword);
    
    // ADMIN용: 같은 부서 아이템 검색
    @Query("SELECT i FROM Item i WHERE " +
           "i.departmentId = :departmentId AND " +
           "(i.itemName LIKE %:keyword% OR i.description LIKE %:keyword%)")
    List<Item> searchByKeywordAndDepartment(
        @Param("keyword") String keyword, 
        @Param("departmentId") String departmentId
    );
    
    // USER용: 자기가 생성한 아이템 검색
    @Query("SELECT i FROM Item i WHERE " +
           "i.createdBy = :user AND " +
           "(i.itemName LIKE %:keyword% OR i.description LIKE %:keyword%)")
    List<Item> searchByKeywordAndCreatedBy(
        @Param("keyword") String keyword, 
        @Param("user") User user
    );
}