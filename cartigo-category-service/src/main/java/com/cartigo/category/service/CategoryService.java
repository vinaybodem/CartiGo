package com.cartigo.category.service;

import com.cartigo.category.dao.CategoryDao;
import com.cartigo.category.entity.Category;

import java.util.List;

public interface CategoryService {


    CategoryDao createCategory(Category category);

    CategoryDao updateCategory(String name, CategoryDao category);

    Category getCategoryById(Long id);

    List<Category> getAllCategories();

    List<Category> searchByCategoryName(String keyword);

    Long getCategoryId(String categoryName);
    void deleteCategory(String name);

    boolean isValidCategory(String name);
}
