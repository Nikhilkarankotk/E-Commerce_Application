package com.nkk.Products.service;


import com.nkk.Products.dto.SubCategoryDTO;

import java.util.List;

public interface ISubCategoryService {

    List<SubCategoryDTO> getAllSubCategories();

    List<SubCategoryDTO> getSubCategoriesByCategoryId(Long id);

    SubCategoryDTO addSubCategory(SubCategoryDTO subcategory);

    SubCategoryDTO updateSubCategory(Long id, SubCategoryDTO updatedCategory);

    void deleteSubCategory(Long id);

}
