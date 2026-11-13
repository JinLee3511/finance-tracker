package com.github.jinlee3511.finance_tracker.repository;

import com.github.jinlee3511.finance_tracker.entity.Category;
import com.github.jinlee3511.finance_tracker.entity.Transaction;
import com.github.jinlee3511.finance_tracker.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByTransactionDate(LocalDate localDate);
    List<Transaction> findByTransactionDateBetween(LocalDate startDate, LocalDate endDate);
    List<Transaction> findByTransactionDateBetweenOrderByTransactionDateDesc(LocalDate startDate,
                                                                                    LocalDate endDate);
    List<Transaction> findByTransactionDateAndTransactionType(LocalDate localDate,
                                                                     TransactionType transactionType);

    List<Transaction> findByCategory(Long categoryId);
    List<Transaction> findByCategoryAndTransactionDate(Category category, LocalDate localDate);

}
