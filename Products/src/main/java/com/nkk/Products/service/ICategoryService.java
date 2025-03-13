package com.nkk.Products.service;


import com.nkk.Products.dto.ProductDTO;
import com.nkk.Products.entity.Category;

import java.util.List;

public interface ICategoryService {

    List<Category> getAllCategories();

    Category getCategoryById(Long id);

    Category addCategory(Category category);

    Category updateCategory(Long id, Category updatedCategory);

    void deleteCategory(Long id);

}
