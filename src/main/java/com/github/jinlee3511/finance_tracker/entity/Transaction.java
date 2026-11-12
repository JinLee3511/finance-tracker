package com.github.jinlee3511.finance_tracker.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA Spec + Need Default Constructor for proxy
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // category_id FK (Foreign Key) + LAZY in order to get category when actually needed..
    // @Column(nullable = false) // should not use @Column when relationship exists..
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, precision = 15, scale = 2) // precision: total length, scale: decimals..
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR) // create column as VARCHAR not ENUM type
    @Column(nullable = false, length = 20)
    private TransactionType transactionType;

    @Column(nullable = false)
    private LocalDate transactionDate;

    private String description;


    // Force category & transaction type & amount & date to exist
    public Transaction(Category category, TransactionType transactionType,
                       BigDecimal amount, LocalDate transactionDate) {
        validateCategory(category);
        validateAmount(amount);
        validateTransactionType(transactionType);
        validateTransactionDate(transactionDate);

        this.category = category;
        this.transactionType = transactionType;
        this.amount = amount;
        this.transactionDate = transactionDate;
    }

    public void changeDescription(String description) {
        validateDescription(description);
        this.description = description;
    }

    private void validateCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category is null");
        }
    }

    private void validateTransactionType(TransactionType transactionType) {
        if (transactionType == null) {
            throw new IllegalArgumentException("Transaction Type is null");
        }
    }

    private void validateAmount(BigDecimal amount) {
        // Always use compareTo for BigDecimal (with equals, diff scale (decimals..) will say false although seems same value)
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) { // amount < 0 -> return -1
            throw new IllegalArgumentException("Amount must be positive");
        }
    }

    private void validateTransactionDate(LocalDate transactionDate) {
        if (transactionDate == null) {
            throw new IllegalArgumentException("Transaction Date is null");
        }
    }

    private void validateDescription(String description) {
        if (description == null) {
            throw new IllegalArgumentException("Description is null");
        }
    }
}
