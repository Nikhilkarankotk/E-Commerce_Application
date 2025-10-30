package com.nkk.Products.mapper;

import com.nkk.Products.dto.CategoryDTO;
import com.nkk.Products.dto.SubCategoryDTO;
import com.nkk.Products.entity.Category;
import com.nkk.Products.entity.SubCategory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategoryMapper {

    public CategoryDTO mapToCategoryDTO(Category category) {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCategoryId(category.getCategoryId());
        categoryDTO.setCategoryName(category.getCategoryName());
        // Include subcategories (if any)
        if (category.getSubCategories() != null && !category.getSubCategories().isEmpty()) {
            List<String> subCategoryNames = category.getSubCategories()
                    .stream()
                    .map(SubCategory::getSubCategoryName)
                    .collect(Collectors.toList());
            categoryDTO.setSubCategoryNames(subCategoryNames);
        }

        return categoryDTO;
    }

    public Category mapToCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setCategoryId(categoryDTO.getCategoryId());
        category.setCategoryName(categoryDTO.getCategoryName());
        return category;
    }
    public List<CategoryDTO> mapToCategoryDTOList(List<Category> categories) {
        return categories.stream()
                .map(this::mapToCategoryDTO)
                .collect(Collectors.toList());
    }

    private SubCategoryDTO mapToSubCategoryDTO(SubCategory subCategory) {
        SubCategoryDTO dto = new SubCategoryDTO();
        dto.setSubCategoryId(subCategory.getSubCategoryId());
        dto.setSubCategoryName(subCategory.getSubCategoryName());
        return dto;
    }

}
