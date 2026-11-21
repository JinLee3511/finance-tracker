package com.github.jinlee3511.finance_tracker.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

public class CategoryTest {
    private static final String NAME = "Test";
    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category(NAME);
    }

    @Nested
    @DisplayName("constructor")
    class Create {

        @Test
        @DisplayName("Create Success")
        void createSuccess() {
            Category category = new Category(NAME);

            assertThat(category.getId()).isNull(); // id create by DB
            assertThat(category.getName()).isEqualTo(NAME);
        }

        @ParameterizedTest(name = "name = {0}")
        @ValueSource(strings = {"Test", " Test", "tESt", " tesT ", "   \n test   \t "})
        @DisplayName("Create Success - name format changed")
        void createSuccess_nameFormatChanged(String value) {
            Category category = new Category(value);

            assertThat(category.getName()).isEqualTo(NAME);
        }

        @ParameterizedTest(name = "name = [{0}] -> [{1}]")
        @CsvSource({"a, A", "카테고리, 카테고리", "1, 1", "*, *", "   a , A"})
        @DisplayName("Create Success - name format edge cases")
        void createSuccess_nameFormatChanged_edgeCases(String input, String expected) {
            Category category = new Category(input);
            assertThat(category.getName()).isEqualTo(expected);
        }

        @Test
        @DisplayName("Create Fail - name is null")
        void createFail_nameNull() {
            assertThatThrownBy(() -> new Category(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Name Cannot be empty");
        }

        @ParameterizedTest(name = "name = [{0}]")
        @ValueSource(strings = {"", " ", "\n", "\t", " \n \t"})
        @DisplayName("Create Fail - name is blank")
        void createFail_nameBlank(String value) {
            assertThatThrownBy(() -> new Category(value))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Name Cannot be empty");
        }

    }

    @Nested
    @DisplayName("changeName")
    class Update {

        @Test
        @DisplayName("Change Name Success")
        void changeName_success() {
            category.changeName("Test2");

            assertThat(category.getName()).isEqualTo("Test2");
        }

        @ParameterizedTest(name = "name = {0}")
        @ValueSource(strings = {"Test2", " Test2", "tESt2", " tesT2 ", "   \n test2   \t "})
        @DisplayName("Change Success - name format changed")
        void changeName_success_nameFormatChanged(String value) {
            category.changeName(value);
            assertThat(category.getName()).isEqualTo("Test2");
        }

        @ParameterizedTest(name = "name = [{0}] -> [{1}]")
        @CsvSource({"a, A", "카테고리, 카테고리", "1, 1", "*, *", "   a , A"})
        @DisplayName("Change Success - name format edge cases")
        void changeName_nameFormatChanged_edgeCases(String input, String expected) {
            category.changeName(input);
            assertThat(category.getName()).isEqualTo(expected);
        }

        @Test
        @DisplayName("Change Name Fail - name is null")
        void changeName_fail_nameNull() {
            assertThatThrownBy(() -> category.changeName(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Name Cannot be empty");
        }

        @ParameterizedTest(name = "name = [{0}]")
        @ValueSource(strings = {"", " ", "\n", "\t", " \n \t"})
        @DisplayName("Change Name Fail - name blank")
        void changeName_fail_nameBlank(String value) {
            assertThatThrownBy(() -> category.changeName(value))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Name Cannot be empty");
        }

        @Test
        @DisplayName("Change Name Fail - name not change")
        void changeName_fail_keepsOriginalName() {
            assertThatThrownBy(() -> category.changeName(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Name Cannot be empty");

            assertThat(category.getName()).isEqualTo(NAME);
        }
    }
}
