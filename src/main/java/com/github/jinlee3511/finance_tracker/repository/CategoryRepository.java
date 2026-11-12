package com.github.jinlee3511.finance_tracker.repository;

import com.github.jinlee3511.finance_tracker.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);
}
