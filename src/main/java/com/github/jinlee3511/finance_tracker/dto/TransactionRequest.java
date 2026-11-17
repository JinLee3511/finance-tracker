package com.github.jinlee3511.finance_tracker.dto;

import com.github.jinlee3511.finance_tracker.entity.TransactionType;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionRequest(
        @NotNull
        @Positive // only allow positive numbers
        Long categoryId,

        @NotNull
        @Positive
        @Digits(integer = 13, fraction = 2) // 13 ints with 2 decimals (matches DB decimal(15,2))
        BigDecimal amount,

        @NotNull
        TransactionType transactionType,

        @NotNull
        LocalDate transactionDate,

        @Size(max = 255, message = "Description must be 255 characters or less") // JPA default 255
        String description
) {}
