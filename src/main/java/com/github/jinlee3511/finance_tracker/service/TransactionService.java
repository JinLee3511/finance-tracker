package com.github.jinlee3511.finance_tracker.service;

import com.github.jinlee3511.finance_tracker.entity.Category;
import com.github.jinlee3511.finance_tracker.entity.Transaction;
import com.github.jinlee3511.finance_tracker.entity.TransactionType;
import com.github.jinlee3511.finance_tracker.exception.TransactionNotFoundException;
import com.github.jinlee3511.finance_tracker.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryService categoryService;

    // Create
    @Transactional
    public Long createTransaction(Long category_id, TransactionType transactionType,
                                  BigDecimal amount, LocalDate transactionDate, String description) {
        Category category = categoryService.getCategoryById(category_id); // No Category -> 404
        Transaction transaction = new Transaction(category, transactionType, amount, transactionDate, description);
        transactionRepository.save(transaction);
        return transaction.getId();
    }

    // Select All
    public List<Transaction> getAllTransaction() {
        return transactionRepository.findAll();
    }

    // Select By Id
    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction Not Found: " + id));
    }

    // Select By Dates
    public List<Transaction> getTransactionsByDates(LocalDate localDate) {
        return transactionRepository.findByTransactionDate(localDate);
    }

    public List<Transaction> getTransactionsByDatesInBetween(LocalDate start, LocalDate end) {
        return transactionRepository.findByTransactionDateBetween(start, end);
    }

    public List<Transaction> getTransactionsByDatesInBetweenLastOrder(LocalDate start, LocalDate end) {
        return transactionRepository.findByTransactionDateBetweenOrderByTransactionDateDesc(start, end);
    }

    // Modify Transaction
    @Transactional
    public void updateTransaction(Long id, Long category_id, BigDecimal amount, TransactionType transactionType,
                                  LocalDate transactionDate, String description) {
        Transaction transaction = getTransactionById(id); // not found -> 404
        Category category = categoryService.getCategoryById(category_id); // not found -> 404
        transaction.updateTransaction(category, amount, transactionType, transactionDate, description);
    }

    // Delete
    @Transactional
    public void deleteTransactionById(Long id) {
        Transaction transaction = getTransactionById(id);
        transactionRepository.delete(transaction);
    }
}
