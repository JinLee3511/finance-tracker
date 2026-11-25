package com.github.jinlee3511.finance_tracker.service;

import com.github.jinlee3511.finance_tracker.entity.Category;
import com.github.jinlee3511.finance_tracker.entity.Transaction;
import com.github.jinlee3511.finance_tracker.entity.TransactionType;
import com.github.jinlee3511.finance_tracker.exception.CategoryNotFoundException;
import com.github.jinlee3511.finance_tracker.repository.TransactionRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    TransactionRepository transactionRepository;

    @Mock
    CategoryService categoryService;

    @InjectMocks
    TransactionService transactionService;

    @Test
    void createTransaction_whenCategoryNotFound_throwsAndDoesNotSave() {
        // given
        when(categoryService.getCategoryById(99L))
                .thenThrow(new CategoryNotFoundException("Category Not Found: 99"));

        // when & then
        assertThatThrownBy(() ->
                        // keep local date fixed for testing -> always want same results
                        transactionService.createTransaction(99L, TransactionType.EXPENSE,
                                new BigDecimal("100.00"), LocalDate.of(2026,11,12),
                                "Lunch")
                )
                .isInstanceOf(CategoryNotFoundException.class);
                //.hasMessage("Category Not Found: 99"); // no need to check, we already know..

        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void createTransaction_whenCategoryExists_savesTransactionWithCorrectValues() {
        // given
        Category category = new Category("Food");
        when(categoryService.getCategoryById(1L)).thenReturn(category);

        // when
        transactionService.createTransaction(1L, TransactionType.EXPENSE, new BigDecimal("100.25"),
                LocalDate.of(2026, 11, 11), "Lunch");

        // then
        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

        // 1. Verify if it got saved
        verify(transactionRepository).save(captor.capture()); // capture the Transaction that was saved

        // 2. Verify Captured Transaction equals to the Transaction created
        Transaction saved = captor.getValue();
        assertThat(saved.getCategory()).isSameAs(category); // ==
        assertThat(saved.getTransactionDate()).isEqualTo(LocalDate.of(2026, 11, 11)); // equals()
        assertThat(saved.getTransactionType()).isEqualTo(TransactionType.EXPENSE);
        assertThat(saved.getDescription()).isEqualTo("Lunch");
        assertThat(saved.getAmount()).isEqualByComparingTo(new BigDecimal("100.25")); // compareTo() == 0
        // compareTo is method from Comparable interface..
    }
}
