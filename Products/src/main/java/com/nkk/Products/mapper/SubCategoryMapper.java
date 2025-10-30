package com.nkk.Products.mapper;


import com.nkk.Products.dto.SubCategoryDTO;
import com.nkk.Products.entity.SubCategory;
import com.nkk.Products.entity.Category;

public class SubCategoryMapper {

    public static SubCategoryDTO MaptoSubCategoryDTO(SubCategory subCategory) {
        SubCategoryDTO dto = new SubCategoryDTO();
        dto.setSubCategoryId(subCategory.getSubCategoryId());
        dto.setSubCategoryName(subCategory.getSubCategoryName());
        if (subCategory.getParentCategory()!= null) {
            dto.setCategoryId(subCategory.getParentCategory().getCategoryId());
        }
        return dto;
    }

    public static SubCategory MaptoSubCategory(SubCategoryDTO subCategoryDTO, Category category) {
        SubCategory subCategory = new SubCategory();
        subCategory.setSubCategoryId(subCategoryDTO.getSubCategoryId());
        subCategory.setSubCategoryName(subCategoryDTO.getSubCategoryName());
        subCategory.setParentCategory(category);
        return subCategory;
    }
}
