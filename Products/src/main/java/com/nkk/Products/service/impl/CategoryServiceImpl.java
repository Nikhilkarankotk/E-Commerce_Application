package com.nkk.Products.service.impl;

import com.nkk.Products.entity.Category;
import com.nkk.Products.exception.ResourceAlreadyExistsException;
import com.nkk.Products.exception.ResourceNotFoundException;
import com.nkk.Products.exception.ParentCategoryNotFoundException;
import com.nkk.Products.repository.CategoryRepository;
import com.nkk.Products.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id" ,id));
    }

    @Transactional
    public Category addCategory(Category category) {
        if (categoryRepository.existsByCategoryName(category.getCategoryName())) {
            throw new ResourceAlreadyExistsException("Category already exists with name: " + category.getCategoryName());
        }
        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategory(Long id, Category updatedCategory) {
        Category existingCategory = getCategoryById(id);
        existingCategory.setCategoryName(updatedCategory.getCategoryName());
        if (updatedCategory.getParentCategory() != null) {
            Category parentCategory = categoryRepository.findById(updatedCategory.getParentCategory().getCategoryId())
                    .orElseThrow(() -> new ParentCategoryNotFoundException("Parent category not found"));
            existingCategory.setParentCategory(parentCategory);
        }
        return categoryRepository.save(existingCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
    }
}
