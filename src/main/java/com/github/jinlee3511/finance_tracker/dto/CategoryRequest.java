package com.github.jinlee3511.finance_tracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
        @NotBlank(message = "Category Name is required")
        @Size(max = 50, message = "Category name must be 50 characters or less")
        String name
) {}
