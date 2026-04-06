package com.cartigo.category.service.impl;

import com.cartigo.category.dao.CategoryDao;
import com.cartigo.category.entity.Category;
import com.cartigo.category.exception.ResourceNotFoundException;
import com.cartigo.category.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void createCategory_successful() {
        Category inputCategory = new Category();
        inputCategory.setName("Electronics");
        inputCategory.setDescription("Electronic items");
        inputCategory.setActive(true);

        Category savedCategory = new Category();
        savedCategory.setId(1L);
        savedCategory.setName("Electronics");
        savedCategory.setDescription("Electronic items");
        savedCategory.setActive(true);

        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        CategoryDao result = categoryService.createCategory(inputCategory);

        assertNotNull(result);
        assertEquals("Electronic items", result.getName());
        assertEquals("Electronics", result.getDescription());
        assertTrue(result.isActive());

        verify(categoryRepository).save(inputCategory);
    }

    @Test
    void updateCategory_whenCategoryExists_updatesAndReturnsDao() {
        Category existingCategory = new Category();
        existingCategory.setId(1L);
        existingCategory.setName("Electronics");
        existingCategory.setDescription("Old Description");
        existingCategory.setActive(true);

        CategoryDao updateRequest = new CategoryDao("New Description", "Electronics", false);

        Category savedCategory = new Category();
        savedCategory.setId(1L);
        savedCategory.setName("Electronics");
        savedCategory.setDescription("New Description");
        savedCategory.setActive(false);

        when(categoryRepository.findByName("Electronics")).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        CategoryDao result = categoryService.updateCategory("Electronics", updateRequest);

        assertNotNull(result);
        assertEquals("New Description", result.getName());
        assertEquals("Electronics", result.getDescription());
        assertFalse(result.isActive());

        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void updateCategory_whenCategoryDoesNotExist_throwsException() {
        when(categoryRepository.findByName("NonExistent")).thenReturn(Optional.empty());
        CategoryDao updateRequest = new CategoryDao("New Description", "Electronics", false);

        assertThrows(ResourceNotFoundException.class, () -> categoryService.updateCategory("NonExistent", updateRequest));
    }

    @Test
    void getCategoryById_whenExists_returnsCategory() {
        Category category = new Category();
        category.setId(1L);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        Category result = categoryService.getCategoryById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getCategoryById_whenDoesNotExist_throwsException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(1L));
    }

    @Test
    void getAllCategories_returnsList() {
        Category category = new Category();
        category.setId(1L);
        when(categoryRepository.findAll()).thenReturn(List.of(category));

        List<Category> result = categoryService.getAllCategories();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void getAllCategories_whenEmpty_returnsEmptyList() {
        when(categoryRepository.findAll()).thenReturn(Collections.emptyList());

        List<Category> result = categoryService.getAllCategories();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void searchByCategoryName_returnsMatchingCategories() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Laptop");
        when(categoryRepository.searchCategoriesByNameContainingIgnoreCase("lap")).thenReturn(List.of(category));

        List<Category> result = categoryService.searchByCategoryName("lap");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).getName());
    }

    @Test
    void getCategoryId_whenExists_returnsId() {
        Category category = new Category();
        category.setId(10L);
        when(categoryRepository.findByName("Electronics")).thenReturn(Optional.of(category));

        Long resultId = categoryService.getCategoryId("Electronics");

        assertNotNull(resultId);
        assertEquals(10L, resultId);
    }

    @Test
    void getCategoryId_whenDoesNotExist_throwsException() {
        when(categoryRepository.findByName("NonExistent")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryId("NonExistent"));
    }

    @Test
    void deleteCategory_successful() {
        Category category = new Category();
        category.setId(10L);
        when(categoryRepository.findByName("Electronics")).thenReturn(Optional.of(category));
        when(categoryRepository.findById(10L)).thenReturn(Optional.of(category));

        categoryService.deleteCategory("Electronics");

        verify(categoryRepository).delete(category);
    }

    @Test
    void deleteCategory_whenDoesNotExist_throwsException() {
        when(categoryRepository.findByName("NonExistent")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategory("NonExistent"));
        verify(categoryRepository, never()).delete(any());
    }

    @Test
    void isValidCategory_whenExists_returnsTrue() {
        when(categoryRepository.existsById(1L)).thenReturn(true);

        boolean result = categoryService.isValidCategory(1L);

        assertTrue(result);
    }

    @Test
    void isValidCategory_whenDoesNotExist_returnsFalse() {
        when(categoryRepository.existsById(1L)).thenReturn(false);

        boolean result = categoryService.isValidCategory(1L);

        assertFalse(result);
    }
}
