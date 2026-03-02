package com.cartigo.category.controller;

import com.cartigo.category.common.ApiResponse;
import com.cartigo.category.dao.CategoryDao;
import com.cartigo.category.entity.Category;
import com.cartigo.category.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("api/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("addcategory")
    public ResponseEntity<ApiResponse<CategoryDao>> createCategory(@RequestBody Category category){
        return new ResponseEntity<>(ApiResponse.ok("Created",categoryService.createCategory(category)), HttpStatus.CREATED);
    }

    @PutMapping("update/{category_name}")
    public ResponseEntity<CategoryDao> updateCategory(@PathVariable String category_name , @RequestBody CategoryDao category){
        return new ResponseEntity<>(categoryService.updateCategory(category_name,category),HttpStatus.OK);
    }

    @GetMapping("get/{category_id}")
    public ResponseEntity<ApiResponse<Category>> getCategoryById(@PathVariable Long category_id) {
        return new ResponseEntity<>(ApiResponse.ok("Category", categoryService.getCategoryById(category_id)),HttpStatus.OK);
    }
    @DeleteMapping("delete/{category_name}")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable String category_name) {
        categoryService.deleteCategory(category_name);
        return new ResponseEntity<>(ApiResponse.ok("Deleted", null),HttpStatus.NO_CONTENT);
    }
    @GetMapping("validate/{category_id}")
    public boolean validateCategory(@PathVariable Long category_id) {
        return categoryService.isValidCategory(category_id);
    }
    @GetMapping("get")
    public ResponseEntity<ApiResponse<List<Category>>>  searchByCategoryName(@RequestParam String keyword){
        if(keyword != null && !keyword.isBlank())
            return new ResponseEntity<>(ApiResponse.ok("Search results",categoryService.searchByCategoryName(keyword)),HttpStatus.OK);
        return new ResponseEntity<>(ApiResponse.ok(" All Categories", categoryService.getAllCategories()),HttpStatus.OK);
    }

    @GetMapping("getId/{category_name}")
    public Long getCategroyId(@PathVariable String category_name){
        return categoryService.getCategoryId(category_name);
    }
}
