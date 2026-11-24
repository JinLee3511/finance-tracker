package com.github.jinlee3511.finance_tracker.service;

import com.github.jinlee3511.finance_tracker.entity.Category;
import com.github.jinlee3511.finance_tracker.exception.CategoryNotFoundException;
import com.github.jinlee3511.finance_tracker.exception.DuplicateCategoryException;
import com.github.jinlee3511.finance_tracker.repository.CategoryRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    CategoryRepository categoryRepository; // fake repo

    @InjectMocks
    CategoryService categoryService; // above mocked repo will be injected here

    @Test
    void createCategory_whenNameIsNew_savesCategory() {
        // given
        when(categoryRepository.existsByNameIgnoreCase("food")).thenReturn(false);

        // when
        categoryService.createCategory("  food ");
        // System.out.println(category.getId()); // null (mock situation, no DB)

        // then
        // verify(categoryRepository, times(1)).save(any(Category.class)); // times(1) is default..
        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);

        // 1. Verify only one time saved
        verify(categoryRepository).save(captor.capture()); // catch the Category being saved
        Category saved = captor.getValue(); // get actual Category being saved

        // 2. Verify Correct Category Saved
        assertThat(saved.getName()).isEqualTo("Food");
    }

    @Test
    void createCategory_whenNameExists_DuplicateCategoryException() {
        // given
        when(categoryRepository.existsByNameIgnoreCase("food")).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> categoryService.createCategory("  food"))
                .isInstanceOf(DuplicateCategoryException.class)
                .hasMessage("Category Already Exists: food");

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void getCategoryById_whenIdDoesNotExist_CategoryNotFoundException() {
        // given
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> categoryService.getCategoryById(99L))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessage("Category Not Found: 99");
    }

    @Test
    void renameCategory_whenSameNameDifferentCase_succeeds() {
        // given
        Category foodCategory = new Category("Food");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(foodCategory));

        // when & then
        assertThatCode(() -> categoryService.renameCategory(1L, "food")).doesNotThrowAnyException();
        verify(categoryRepository, never()).existsByNameIgnoreCase(anyString());
    }

    @Test
    void renameCategory_whenNameAlreadyExists_DuplicateCategoryException() {
        // given
        Category category = new Category("Rent");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByNameIgnoreCase("Food")).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> categoryService.renameCategory(1L, " Food"))
                .isInstanceOf(DuplicateCategoryException.class)
                .hasMessage("Category Already Exists: Food");
        verify(categoryRepository).existsByNameIgnoreCase("Food");
        assertThat(category.getName()).isEqualTo("Rent");
    }

    @Test
    void deleteCategoryById_whenExists_succeeds() {
        // given
        Category category = new Category("Food");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        // when
        categoryService.deleteCategoryById(1L);

        // then
        verify(categoryRepository).delete(category);

        // Do not check if actually deleted / correct entity deleted
        // -> "verify" checks the service passed the right entity to delete().
        // -> Whether it's actually gone from the DB is an integration-test concern (mock doesn't delete).
    }

    @Test
    void deleteCategoryById_whenNotExists_CategoryNotFoundException() {
        // given
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> categoryService.deleteCategoryById(1L))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessage("Category Not Found: 1");
        verify(categoryRepository, never()).delete(any(Category.class));
    }
}
