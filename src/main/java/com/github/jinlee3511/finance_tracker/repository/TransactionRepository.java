package com.github.jinlee3511.finance_tracker.repository;

import com.github.jinlee3511.finance_tracker.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
