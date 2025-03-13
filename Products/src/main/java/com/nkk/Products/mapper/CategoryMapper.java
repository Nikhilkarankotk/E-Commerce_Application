package com.nkk.Products.mapper;

import com.nkk.Products.dto.CategoryDTO;
import com.nkk.Products.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryDTO mapToCategoryDTO(Category category) {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCategoryId(category.getCategoryId());
        categoryDTO.setCategoryName(category.getCategoryName());
        if(category.getParentCategory() != null) {
            categoryDTO.setParentCategoryId(category.getParentCategory().getCategoryId());
        }
        return categoryDTO;
    }

    public Category mapToCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setCategoryId(categoryDTO.getCategoryId());
        category.setCategoryName(categoryDTO.getCategoryName());
        if(categoryDTO.getParentCategoryId() != null) {
            Category parentCategory = new Category();
            parentCategory.setCategoryId(categoryDTO.getParentCategoryId());
            category.setParentCategory(parentCategory);
        }
        return category;
    }
}
