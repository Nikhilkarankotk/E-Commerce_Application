package com.nkk.Products.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryDTO {

    private Long categoryId;

    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name can't exceed 100 characters")
    private String categoryName;

    private Long parentCategoryId;
}
