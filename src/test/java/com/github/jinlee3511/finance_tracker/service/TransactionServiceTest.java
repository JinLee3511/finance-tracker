package com.github.jinlee3511.finance_tracker.service;

import com.github.jinlee3511.finance_tracker.entity.Transaction;
import com.github.jinlee3511.finance_tracker.entity.TransactionType;
import com.github.jinlee3511.finance_tracker.exception.CategoryNotFoundException;
import com.github.jinlee3511.finance_tracker.repository.TransactionRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.*;

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
        Assertions.assertThatThrownBy(() ->
                        // keep local date fixed for testing -> always want same results
                        transactionService.createTransaction(99L, TransactionType.EXPENSE,
                                new BigDecimal("100.00"), LocalDate.of(2026,11,12),
                                "Lunch")
                )
                .isInstanceOf(CategoryNotFoundException.class);
                //.hasMessage("Category Not Found: 99"); // no need to check, we already know..

        verify(transactionRepository, never()).save(any(Transaction.class));
    }
}
