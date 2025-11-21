package com.labelai.repository;

import com.labelai.entity.Item;
import com.labelai.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, UUID> {
    List<Item> findByCreatedByOrderByCreatedAtDesc(User user);
    List<Item> findAllByOrderByCreatedAtDesc();
}
