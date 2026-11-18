package com.github.jinlee3511.finance_tracker.dto;

import com.github.jinlee3511.finance_tracker.entity.Transaction;
import com.github.jinlee3511.finance_tracker.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionResponse(
    Long id,
    Long categoryId, // do not return Category itself, LAZY exception (DB Session already closed..)
    BigDecimal amount,
    TransactionType transactionType,
    LocalDate transactionDate,
    String description
) {
    // Entity -> DTO
    public static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getCategory().getId(),
                transaction.getAmount(),
                transaction.getTransactionType(),
                transaction.getTransactionDate(),
                transaction.getDescription()
        );
    }
}
