package com.nkk.Cart.Dto;

import lombok.Data;

@Data
public class CategoryDTO {
    private Long categoryId;
    private String categoryName;
    private Long parentCategoryId;
}
