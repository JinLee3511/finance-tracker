package com.github.jinlee3511.finance_tracker.controller;

import com.github.jinlee3511.finance_tracker.dto.CategoryRequest;
import com.github.jinlee3511.finance_tracker.dto.CategoryResponse;
import com.github.jinlee3511.finance_tracker.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController // return type body
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/{id}")
    public CategoryResponse getCategory(@PathVariable Long id) {
        // CategoryNotFoundException is not caught here
        // -> handled globally in @RestControllerAdvice (404)
        return CategoryResponse.from(categoryService.getCategoryById(id));
    }

    @GetMapping // /api/categories -> means get all
    public List<CategoryResponse> getAllCategories() {
        return categoryService.getCategories()
                .stream()
                .map(CategoryResponse::from)
                .toList(); // automatically 200
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody @Valid CategoryRequest categoryRequest) {
        // @Valid: Checks the annotations on DTO
        // -> invalid: MethodArgumentNotValidException: 400 bad request automatically..
        Long categoryId = categoryService.createCategory(categoryRequest.name());
        CategoryResponse categoryResponse = CategoryResponse.from(categoryService.getCategoryById(categoryId));
        // created: 201 http status
        return ResponseEntity.created(URI.create("/api/categories/" + categoryId)).body(categoryResponse);
    }

    @PutMapping("/{id}")
    public CategoryResponse renameCategory(@PathVariable Long id,
                                                           @RequestBody @Valid CategoryRequest categoryRequest) {
        categoryService.renameCategory(id, categoryRequest.name());
        return CategoryResponse.from(categoryService.getCategoryById(id)); // automatically 200
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategoryById(id);
        return ResponseEntity.noContent().build(); // 204, no body
    }
}
