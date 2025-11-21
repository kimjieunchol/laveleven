package com.labelai.repository;

import com.labelai.entity.Item;
import com.labelai.entity.Translate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TranslateRepository extends JpaRepository<Translate, UUID> {
    Optional<Translate> findFirstByItem(Item item);
}
