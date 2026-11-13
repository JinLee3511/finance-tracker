package com.github.jinlee3511.finance_tracker.service;

import com.github.jinlee3511.finance_tracker.entity.Category;
import com.github.jinlee3511.finance_tracker.exception.CategoryNotFoundException;
import com.github.jinlee3511.finance_tracker.exception.DuplicateCategoryException;
import com.github.jinlee3511.finance_tracker.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor // DI purpose, auto-inject required field values from bean (final)
@Transactional(readOnly = true) // search efficiency good
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // 1. Create
    @Transactional // Entire service is readOnly, make sure this method is allowed with INSERT
    public Long createCategory(String name) {
        // If called simultaneously, both may pass this check,
        // but the UNIQUE constraint on name (DB side) will block the second insert
        // -> DataIntegrityViolationException, handled in global exception handler (@RestControllerAdvice)
        if (categoryRepository.existsByName(name)) {
            // do not return, raise error so that Controller can catch and throw 409 (conflict)
            // ALWAYS Runtime Exception!! -> rolls back commit, but Exception commits..
            throw new DuplicateCategoryException("Category Already Exists: " + name);
        }

        Category category = new Category(name);
        categoryRepository.save(category);
        return category.getId(); // convention to return ID when inserted
    }

    // 2. Select All
    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    // 3. Select one
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category Not Found: " + id));
    }

    // 4. Change name
    @Transactional
    public void renameCategory(Long id, String name) {
        Category category = getCategoryById(id); // select query happens, not lazy loading. All fields available this time

        if (!category.getName().equals(name) && categoryRepository.existsByName(name)) {
            throw new DuplicateCategoryException("Category Already Exists: " + name);
        }

        category.changeName(name);
        // with @Transactional, JPA auto checks the Entity status and if something changes
        // -> it UPDATES with out save (DIRTY CHECKING)
        // categoryRepository.save(category);
    }

    // 5. delete
    @Transactional
    public void deleteCategoryById(Long id) {
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
    }
}
