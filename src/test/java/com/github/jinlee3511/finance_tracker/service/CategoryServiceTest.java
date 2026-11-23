package com.github.jinlee3511.finance_tracker.service;

import com.github.jinlee3511.finance_tracker.entity.Category;
import com.github.jinlee3511.finance_tracker.repository.CategoryRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

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
    void renameCategory_whenSameNameDifferentCase_succeeds() {
        // given
        Category foodCategory = new Category("Food");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(foodCategory));

        // when & then
        assertThatCode(() -> categoryService.renameCategory(1L, "food")).doesNotThrowAnyException();
        verify(categoryRepository, never()).existsByNameIgnoreCase(anyString());
    }
}
