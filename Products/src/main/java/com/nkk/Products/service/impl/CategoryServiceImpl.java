package com.nkk.Products.service.impl;

import com.nkk.Products.dto.CategoryDTO;
import com.nkk.Products.entity.Category;
import com.nkk.Products.exception.ResourceAlreadyExistsException;
import com.nkk.Products.exception.ResourceNotFoundException;
import com.nkk.Products.exception.ParentCategoryNotFoundException;
import com.nkk.Products.mapper.CategoryMapper;
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

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categoryMapper.mapToCategoryDTOList(categories);
    }

    @Override
    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        return categoryMapper.mapToCategoryDTO(category);
    }

    @Transactional
    @Override
    public CategoryDTO addCategory(CategoryDTO categoryDTO) {
        if (categoryRepository.existsByCategoryName(categoryDTO.getCategoryName())) {
            throw new ResourceAlreadyExistsException("Category already exists with name: " + categoryDTO.getCategoryName());
        }

        Category category = categoryMapper.mapToCategory(categoryDTO);

        // Handle parent category if provided
        if (categoryDTO.getParentCategoryId() != null) {
            Category parent = categoryRepository.findById(categoryDTO.getParentCategoryId())
                    .orElseThrow(() -> new ParentCategoryNotFoundException("Parent category not found"));
            category.setParentCategory(parent);
        }

        Category saved = categoryRepository.save(category);
        return categoryMapper.mapToCategoryDTO(saved);
    }

    @Transactional
    @Override
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        existingCategory.setCategoryName(categoryDTO.getCategoryName());

        if (categoryDTO.getParentCategoryId() != null) {
            Category parentCategory = categoryRepository.findById(categoryDTO.getParentCategoryId())
                    .orElseThrow(() -> new ParentCategoryNotFoundException("Parent category not found"));
            existingCategory.setParentCategory(parentCategory);
        } else {
            existingCategory.setParentCategory(null);
        }

        Category updated = categoryRepository.save(existingCategory);
        return categoryMapper.mapToCategoryDTO(updated);
    }

    @Transactional
    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        categoryRepository.delete(category);
    }
}
