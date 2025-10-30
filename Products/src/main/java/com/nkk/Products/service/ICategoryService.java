package com.nkk.Products.service;


import com.nkk.Products.dto.CategoryDTO;
import com.nkk.Products.entity.Category;

import java.util.List;

public interface ICategoryService {

    List<CategoryDTO> getAllCategories();

    CategoryDTO getCategoryById(Long id);

    CategoryDTO addCategory(CategoryDTO categoryDTO);

    CategoryDTO updateCategory(Long id, CategoryDTO updatedCategory);

    void deleteCategory(Long id);

}
