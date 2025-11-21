package com.labelai.repository;

import com.labelai.entity.Translate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TranslateRepository extends JpaRepository<Translate, String> {
    Optional<Translate> findByItemId(String itemId);
}
