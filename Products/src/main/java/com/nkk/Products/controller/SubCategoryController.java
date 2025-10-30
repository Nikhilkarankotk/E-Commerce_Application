package com.nkk.Products.controller;

import com.nkk.Products.dto.SubCategoryDTO;
import com.nkk.Products.service.ISubCategoryService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RestController
@RequestMapping("/subcategories")
public class SubCategoryController {

    @Autowired
    private ISubCategoryService subCategoryService;

    @PostMapping
    public SubCategoryDTO createSubCategory(@RequestBody SubCategoryDTO dto) {
        return subCategoryService.addSubCategory(dto);
    }

    @PutMapping("/{id}")
    public SubCategoryDTO updateSubCategory(@PathVariable Long id, @RequestBody SubCategoryDTO dto) {
        return subCategoryService.updateSubCategory(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteSubCategory(@PathVariable Long id) {
        subCategoryService.deleteSubCategory(id);
    }

    @GetMapping
    public List<SubCategoryDTO> getAllSubCategories() {
        return subCategoryService.getAllSubCategories();
    }

    @GetMapping("/category/{categoryId}")
    public List<SubCategoryDTO> getSubCategoriesByCategoryId(@PathVariable Long categoryId) {
        return subCategoryService.getSubCategoriesByCategoryId(categoryId);
    }
}
