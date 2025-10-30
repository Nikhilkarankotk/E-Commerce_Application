package com.nkk.Products.repository;

import com.nkk.Products.entity.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {
    List<SubCategory> findByParentCategory_CategoryId(Long categoryId);
    boolean existsBySubCategoryNameAndParentCategory_CategoryId(String subCategoryName, Long categoryId);
}
