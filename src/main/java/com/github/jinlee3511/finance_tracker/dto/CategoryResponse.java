package com.github.jinlee3511.finance_tracker.dto;

import com.github.jinlee3511.finance_tracker.entity.Category;

// record: class for values only
public record CategoryResponse(
    Long id,
    String name
) {
    // Convert Entity -> DTO
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(category.getId(), category.getName());
    }
}
