package com.github.jinlee3511.finance_tracker.service;

import com.github.jinlee3511.finance_tracker.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MockitoBasicsTest {

    @Mock
    CategoryRepository categoryRepository;

    @Test
    void basics() {
        // 1. Asking Without Stub
        System.out.println("1. existsByName(food) = " + categoryRepository.existsByNameIgnoreCase("food"));   // false
        System.out.println("2. findById(1)        = " + categoryRepository.findById(1L));           // Optional.empty

        // 2. Asking With Stub
        when(categoryRepository.existsByNameIgnoreCase("food")).thenReturn(true);
        System.out.println("3. existsByName(food) = " + categoryRepository.existsByNameIgnoreCase("food"));   // true
        System.out.println("4. existsByName(rent) = " + categoryRepository.existsByNameIgnoreCase("rent"));   // false

        // 3. Check Verify
        verify(categoryRepository, never()).deleteAll();
        verify(categoryRepository, times(2)).existsByNameIgnoreCase("food");
    }
}
