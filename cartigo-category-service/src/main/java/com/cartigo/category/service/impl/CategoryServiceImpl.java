package com.cartigo.category.service.impl;

import com.cartigo.category.dao.CategoryDao;
import com.cartigo.category.entity.Category;
import com.cartigo.category.exception.ResourceNotFoundException;
import com.cartigo.category.repository.CategoryRepository;
import com.cartigo.category.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryDao createCategory(Category category) {
        Category saved= categoryRepository.save(category);

        return new CategoryDao(saved.getName(), saved.getDescription(), saved.getActive());
    }

    @Override
    public CategoryDao updateCategory(String name, CategoryDao category) {
        Long id = getCategoryId(name);
        Category existing = getCategoryById(id);
        existing.setName(category.getName());
        existing.setDescription(category.getDescription());
        existing.setActive(category.isActive());
        Category saved = categoryRepository.save(existing);
        return new CategoryDao(saved.getName(), saved.getDescription(), saved.getActive());
    }

    @Override
    public Category getCategoryById(Long id) {
        return  categoryRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Category Not Found"));
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public List<Category> searchByCategoryName(String keyword) {
        return categoryRepository.searchCategoriesByNameContainingIgnoreCase(keyword);
    }

    @Override
    public Long getCategoryId(String categoryName) {
        Category category = categoryRepository.findByName(categoryName).orElseThrow(()->new ResourceNotFoundException("No Such Category exists"));
        return category.getId();
    }

    @Override
    public void deleteCategory(String name) {
        Long id = getCategoryId(name);
        Category existing = getCategoryById(id);
        categoryRepository.delete(existing);
    }


    @Override
    public boolean isValidCategory(Long categoryId) {
        return categoryRepository.existsById(categoryId);
    }
}
