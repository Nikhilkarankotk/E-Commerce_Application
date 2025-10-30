package com.nkk.Products.service.impl;

import com.nkk.Products.dto.SubCategoryDTO;
import com.nkk.Products.entity.Category;
import com.nkk.Products.entity.SubCategory;
import com.nkk.Products.exception.ResourceAlreadyExistsException;
import com.nkk.Products.exception.ResourceNotFoundException;
import com.nkk.Products.mapper.SubCategoryMapper;
import com.nkk.Products.repository.CategoryRepository;
import com.nkk.Products.repository.SubCategoryRepository;
import com.nkk.Products.service.ISubCategoryService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubCategoryServiceImpl implements ISubCategoryService {

    @Autowired
    private SubCategoryRepository subCategoryRepository;

    @Autowired
    private CategoryRepository categoryRepository;

//    public SubCategoryDTO addSubCategory(SubCategoryDTO subCategoryDTO) {
//        if (categoryRepository.existsByCategoryName(subCategoryDTO.getSubCategoryName())) {
//            throw new ResourceAlreadyExistsException("SubCategory already exists with name: " + subCategoryDTO.getSubCategoryName());
//        }
//        Category category = categoryRepository.findById(subCategoryDTO.getCategoryId())
//                .orElseThrow(() -> new RuntimeException("Category not found"));
//        SubCategory subCategory = SubCategoryMapper.MaptoSubCategory(subCategoryDTO, category);
//        return SubCategoryMapper.MaptoSubCategoryDTO(subCategoryRepository.save(subCategory));
//    }

    public SubCategoryDTO addSubCategory(SubCategoryDTO subCategoryDTO) {

        // Check if category exists
        Category category = categoryRepository.findById(subCategoryDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", subCategoryDTO.getCategoryId()));

        // ✅ Check if subcategory with same name already exists within this category
        boolean exists = subCategoryRepository.existsBySubCategoryNameAndParentCategory_CategoryId(
                subCategoryDTO.getSubCategoryName(), subCategoryDTO.getCategoryId()
        );

        if (exists) {
            throw new ResourceAlreadyExistsException(
                    "SubCategory '" + subCategoryDTO.getSubCategoryName() +
                            "' already exists under Category '" + category.getCategoryName() + "'."
            );
        }

        // ✅ Map DTO → Entity
        SubCategory subCategory = SubCategoryMapper.MaptoSubCategory(subCategoryDTO, category);

        // ✅ Save and return DTO
        SubCategory saved = subCategoryRepository.save(subCategory);
        return SubCategoryMapper.MaptoSubCategoryDTO(saved);
    }

    @Override
    @Transactional
    public SubCategoryDTO updateSubCategory(Long id, SubCategoryDTO dto) {
        SubCategory existing = subCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SubCategory not found"));
        existing.setSubCategoryName(dto.getSubCategoryName());
        return SubCategoryMapper.MaptoSubCategoryDTO(subCategoryRepository.save(existing));
    }

    @Override
    @Transactional
    public void deleteSubCategory(Long id) {
        if (!subCategoryRepository.existsById(id)) {
            throw new RuntimeException("SubCategory not found");
        }
        subCategoryRepository.deleteById(id);
    }

    @Override
    public List<SubCategoryDTO> getAllSubCategories() {
        return subCategoryRepository.findAll()
                .stream()
                .map(SubCategoryMapper::MaptoSubCategoryDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SubCategoryDTO> getSubCategoriesByCategoryId(Long categoryId) {
        return subCategoryRepository.findByParentCategory_CategoryId(categoryId)
                .stream()
                .map(SubCategoryMapper::MaptoSubCategoryDTO)
                .collect(Collectors.toList());
    }
}
