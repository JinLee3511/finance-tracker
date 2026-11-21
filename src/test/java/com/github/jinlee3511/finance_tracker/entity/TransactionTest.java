package com.github.jinlee3511.finance_tracker.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionTest {
    private static final LocalDate DATE = LocalDate.of(2020, 4, 20);
    private static final BigDecimal AMOUNT = BigDecimal.valueOf(10200);
    private static final String DESCRIPTION = "Test Transaction";

    private Category category;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        category = new Category("Test");
        transaction = new Transaction(category, TransactionType.EXPENSE, AMOUNT, DATE, DESCRIPTION);
    }

    @Nested
    @DisplayName("constructor")
    class Create {

        @Test
        @DisplayName("Create Success")
        void createSuccess() {
            Transaction transaction = new Transaction(category, TransactionType.EXPENSE, AMOUNT, DATE, DESCRIPTION);

            assertThat(transaction.getId()).isNull(); // id create by DB
            assertThat(transaction.getCategory()).isEqualTo(category);
            assertThat(transaction.getTransactionType()).isEqualTo(TransactionType.EXPENSE);
            assertThat(transaction.getAmount()).isEqualByComparingTo(AMOUNT);
            assertThat(transaction.getTransactionDate()).isEqualTo(DATE);
            assertThat(transaction.getDescription()).isEqualTo(DESCRIPTION);
        }

        @Test
        @DisplayName("Create Success - description is null")
        void createSuccess_descriptionNull() {
            Transaction transaction = new Transaction(category, TransactionType.INCOME, AMOUNT, DATE, null);

            assertThat(transaction.getDescription()).isNull();
        }

        @ParameterizedTest(name = "amount = {0}")
        @ValueSource(strings = {"0.01", "1", "10200.50", "9999999999999.99"})
        @DisplayName("Create Success - amount is positive")
        void createSuccess_amountPositive(String value) {
            BigDecimal amount = new BigDecimal(value);

            Transaction transaction = new Transaction(category, TransactionType.INCOME, amount, DATE, DESCRIPTION);

            assertThat(transaction.getAmount()).isEqualByComparingTo(amount);
        }

        @Test
        @DisplayName("Create Fail - category is null")
        void createFail_categoryNull() {
            assertThatThrownBy(() -> new Transaction(null, TransactionType.EXPENSE, AMOUNT, DATE, DESCRIPTION))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Category is null");
        }

        @Test
        @DisplayName("Create Fail - transaction type is null")
        void createFail_transactionTypeNull() {
            assertThatThrownBy(() -> new Transaction(category, null, AMOUNT, DATE, DESCRIPTION))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Transaction Type is null");
        }

        @Test
        @DisplayName("Create Fail - amount is null")
        void createFail_amountNull() {
            assertThatThrownBy(() -> new Transaction(category, TransactionType.EXPENSE, null, DATE, DESCRIPTION))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Amount must be positive");
        }

        @ParameterizedTest(name = "amount = {0}")
        @ValueSource(strings = {"0", "0.00", "-0.01", "-10200"})
        @DisplayName("Create Fail - amount is not positive")
        void createFail_amountNotPositive(String value) {
            BigDecimal amount = new BigDecimal(value);

            assertThatThrownBy(() -> new Transaction(category, TransactionType.EXPENSE, amount, DATE, DESCRIPTION))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Amount must be positive");
        }

        @Test
        @DisplayName("Create Fail - transaction date is null")
        void createFail_transactionDateNull() {
            assertThatThrownBy(() -> new Transaction(category, TransactionType.EXPENSE, AMOUNT, null, DESCRIPTION))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Transaction Date is null");
        }
    }

    @Nested
    @DisplayName("updateTransaction")
    class Update {

        @Test
        @DisplayName("Update Transaction Success")
        void updateTransaction_success() {
            Category newCategory = new Category("Test2");
            BigDecimal newAmount = new BigDecimal("500.50");
            LocalDate newDate = LocalDate.of(2021, 1, 1);

            transaction.updateTransaction(newCategory, newAmount, TransactionType.INCOME, newDate, "Updated");

            assertThat(transaction.getCategory()).isEqualTo(newCategory);
            assertThat(transaction.getTransactionType()).isEqualTo(TransactionType.INCOME);
            assertThat(transaction.getAmount()).isEqualByComparingTo(newAmount);
            assertThat(transaction.getTransactionDate()).isEqualTo(newDate);
            assertThat(transaction.getDescription()).isEqualTo("Updated");
        }

        @Test
        @DisplayName("Update Transaction Success - description is null")
        void updateTransaction_success_descriptionNull() {
            transaction.updateTransaction(category, AMOUNT, TransactionType.EXPENSE, DATE, null);

            assertThat(transaction.getDescription()).isNull();
        }

        @Test
        @DisplayName("Update Transaction Fail - category is null")
        void updateTransaction_fail_categoryNull() {
            assertThatThrownBy(() -> transaction.updateTransaction(null, AMOUNT, TransactionType.INCOME, DATE, "Updated"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Category is null");
        }

        @Test
        @DisplayName("Update Transaction Fail - transaction type is null")
        void updateTransaction_fail_transactionTypeNull() {
            assertThatThrownBy(() -> transaction.updateTransaction(category, AMOUNT, null, DATE, "Updated"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Transaction Type is null");
        }

        @Test
        @DisplayName("Update Transaction Fail - amount is null")
        void updateTransaction_fail_amountNull() {
            assertThatThrownBy(() -> transaction.updateTransaction(category, null, TransactionType.INCOME, DATE, "Updated"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Amount must be positive");
        }

        @ParameterizedTest(name = "amount = {0}")
        @ValueSource(strings = {"0", "0.00", "-0.01", "-10200"})
        @DisplayName("Update Transaction Fail - amount is not positive")
        void updateTransaction_fail_amountNotPositive(String value) {
            BigDecimal amount = new BigDecimal(value);

            assertThatThrownBy(() -> transaction.updateTransaction(category, amount, TransactionType.INCOME, DATE, "Updated"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Amount must be positive");
        }

        @Test
        @DisplayName("Update Transaction Fail - transaction date is null")
        void updateTransaction_fail_transactionDateNull() {
            assertThatThrownBy(() -> transaction.updateTransaction(category, AMOUNT, TransactionType.INCOME, null, "Updated"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Transaction Date is null");
        }

        @Test
        @DisplayName("Update Transaction Fail - values not change")
        void updateTransaction_fail_keepsOriginalValues() {
            Category newCategory = new Category("Test2");

            assertThatThrownBy(() -> transaction.updateTransaction(newCategory, BigDecimal.ZERO,
                    TransactionType.INCOME, LocalDate.of(2021, 1, 1), "Updated"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Amount must be positive");

            assertThat(transaction.getCategory()).isEqualTo(category);
            assertThat(transaction.getTransactionType()).isEqualTo(TransactionType.EXPENSE);
            assertThat(transaction.getAmount()).isEqualByComparingTo(AMOUNT);
            assertThat(transaction.getTransactionDate()).isEqualTo(DATE);
            assertThat(transaction.getDescription()).isEqualTo(DESCRIPTION);
        }
    }

    @Nested
    @DisplayName("changeDescription")
    class ChangeDescription {

        @Test
        @DisplayName("Change Description Success")
        void changeDescription_success() {
            transaction.changeDescription("New Description");

            assertThat(transaction.getDescription()).isEqualTo("New Description");
        }

        @Test
        @DisplayName("Change Description Success - description is null")
        void changeDescription_success_descriptionNull() {
            transaction.changeDescription(null);

            assertThat(transaction.getDescription()).isNull();
        }

        @ParameterizedTest(name = "description = [{0}]")
        @ValueSource(strings = {"", " ", "\n", "\t"})
        @DisplayName("Change Description Success - description is blank")
        void changeDescription_success_descriptionBlank(String value) {
            transaction.changeDescription(value);

            assertThat(transaction.getDescription()).isEqualTo(value);
        }

        @Test
        @DisplayName("Change Description Success - other values not change")
        void changeDescription_success_keepsOtherValues() {
            transaction.changeDescription("New Description");

            assertThat(transaction.getCategory()).isEqualTo(category);
            assertThat(transaction.getTransactionType()).isEqualTo(TransactionType.EXPENSE);
            assertThat(transaction.getAmount()).isEqualByComparingTo(AMOUNT);
            assertThat(transaction.getTransactionDate()).isEqualTo(DATE);
        }
    }
}
