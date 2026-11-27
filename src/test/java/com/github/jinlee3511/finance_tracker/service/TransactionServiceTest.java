package com.github.jinlee3511.finance_tracker.service;

import com.github.jinlee3511.finance_tracker.entity.Category;
import com.github.jinlee3511.finance_tracker.entity.Transaction;
import com.github.jinlee3511.finance_tracker.entity.TransactionType;
import com.github.jinlee3511.finance_tracker.exception.CategoryNotFoundException;
import com.github.jinlee3511.finance_tracker.exception.TransactionNotFoundException;
import com.github.jinlee3511.finance_tracker.repository.TransactionRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

    @Test
    void getTransactionById_whenIdNotExists_throwsNotFound() {
        // given
        // Note: Cannot set stub for testing item (transactionService in this case) -> it is a real service not a mock!
        // when(transactionService.getTransactionById(1L)).thenThrow(TransactionNotFoundException.class);

        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> transactionService.getTransactionById(1L))
                .isInstanceOf(TransactionNotFoundException.class)
                .hasMessage("Transaction Not Found: " + 1L);
    }

    @Test
    void getTransactionsByDate_whenBothDatesNull_returnsAllSortedByDateDesc() {
        // given
        Category category = new Category("Food");
        Transaction transaction1 = new Transaction(category, TransactionType.EXPENSE, new BigDecimal("1.00"),
                LocalDate.of(2026, 11, 26), "tsx1");
        Transaction transaction2 = new Transaction(category, TransactionType.EXPENSE, new BigDecimal("1.00"),
                LocalDate.of(2026, 11, 27), "tsx2");
        List<Transaction> transactionTests = List.of(transaction2, transaction1);


        when(transactionRepository.findAll(Sort.by(Sort.Direction.DESC, "transactionDate")))
                .thenReturn(transactionTests);

        // when
        List<Transaction> transactionList = transactionService.getTransactionsByDate(null, null);

        // then
        assertThat(transactionList).containsExactly(transaction2, transaction1);
        verifyNoMoreInteractions(transactionRepository); // check if other methods were called except the stub
    }

    @Test
    void getTransactionByDate_whenEndDateNull_returnsTransactionsFromStartDate() {
        // given
        Category category = new Category("Food");
        Transaction transaction2 = new Transaction(category, TransactionType.EXPENSE, new BigDecimal("1.00"),
                LocalDate.of(2026, 11, 27), "tsx2");
        Transaction transaction3 = new Transaction(category, TransactionType.EXPENSE, new BigDecimal("1.00"),
                LocalDate.of(2026, 11, 28), "tsx3");
        List<Transaction> transactionTests = List.of(transaction3, transaction2);
        when(transactionRepository
                .findByTransactionDateGreaterThanEqualOrderByTransactionDateDesc(
                        LocalDate.of(2026, 11, 27)))
                .thenReturn(transactionTests);


        // when
        List<Transaction> transactions = transactionService
                .getTransactionsByDate(LocalDate.of(2026, 11, 27), null);

        // then
        assertThat(transactions).containsExactly(transaction3, transaction2);
        verifyNoMoreInteractions(transactionRepository);
    }

    @Test
    void getTransactionByDate_whenStartDateNull_returnsTransactionsUpToEndDate() {
        // given
        Category category = new Category("Food");
        Transaction transaction2 = new Transaction(category, TransactionType.EXPENSE, new BigDecimal("1.00"),
                LocalDate.of(2026, 11, 26), "tsx2");
        Transaction transaction3 = new Transaction(category, TransactionType.EXPENSE, new BigDecimal("1.00"),
                LocalDate.of(2026, 11, 27), "tsx3");
        List<Transaction> transactionTests = List.of(transaction3, transaction2);
        when(transactionRepository
                .findByTransactionDateLessThanEqualOrderByTransactionDateDesc(
                        LocalDate.of(2026, 11, 27)))
                .thenReturn(transactionTests);


        // when
        List<Transaction> transactions = transactionService
                .getTransactionsByDate(null, LocalDate.of(2026, 11, 27));

        // then
        assertThat(transactions).containsExactly(transaction3, transaction2);
        verifyNoMoreInteractions(transactionRepository);
    }

    @Test
    void getTransactionByDate_whenBothDate_returnsTransactionsBetweenStartToEnd() {
        // given
        Category category = new Category("Food");
        Transaction transaction1 = new Transaction(category, TransactionType.EXPENSE, new BigDecimal("1.00"),
                LocalDate.of(2026, 11, 25), "tsx1");
        Transaction transaction2 = new Transaction(category, TransactionType.EXPENSE, new BigDecimal("1.00"),
                LocalDate.of(2026, 11, 26), "tsx2");
        Transaction transaction3 = new Transaction(category, TransactionType.EXPENSE, new BigDecimal("1.00"),
                LocalDate.of(2026, 11, 27), "tsx3");
        List<Transaction> transactionTests = List.of(transaction3, transaction2, transaction1);
        when(transactionRepository
                .findByTransactionDateBetweenOrderByTransactionDateDesc(
                        LocalDate.of(2026, 11, 25),
                        LocalDate.of(2026, 11, 27)))
                .thenReturn(transactionTests);


        // when
        List<Transaction> transactions = transactionService
                .getTransactionsByDate(
                        LocalDate.of(2026, 11, 25),
                        LocalDate.of(2026, 11, 27)
                );

        // then
        assertThat(transactions).containsExactly(transaction3, transaction2, transaction1);
        verifyNoMoreInteractions(transactionRepository);
    }

    @Test
    void deleteTransaction_whenTransactionExists_succeeds() {
        // given
        Category category = new Category("Food");
        Transaction transaction1 = new Transaction(category, TransactionType.EXPENSE, new BigDecimal("1.00"),
                LocalDate.of(2026, 11, 25), "tsx1");
        when(transactionRepository.findById(1L))
                .thenReturn(Optional.of(transaction1));

        // when
        transactionService.deleteTransactionById(1L);

        // then
        verify(transactionRepository).delete(transaction1);
    }

    @Test
    void deleteTransaction_whenTransactionNotExists_throwsTransactionNotFoundException() {
        // given
        when(transactionRepository.findById(1L))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> transactionService.deleteTransactionById(1L))
                .isInstanceOf(TransactionNotFoundException.class)
                .hasMessage("Transaction Not Found: " + 1L);
        verifyNoMoreInteractions(transactionRepository); // check transactionRepository.delete() not called
    }
}
