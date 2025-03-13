package com.nkk.Products.mapper;

import com.nkk.Products.dto.ProductDTO;
import com.nkk.Products.entity.Category;
import com.nkk.Products.entity.Product;
import com.nkk.Products.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    private static ICategoryService categoryService;

    @Autowired
    public ProductMapper(ICategoryService categoryService) {
        ProductMapper.categoryService = categoryService;
    }

    public static ProductDTO mapToProductDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();productDTO.setProductId(product.getProductId());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setPrice(product.getPrice());
        productDTO.setStockQuantity(product.getStockQuantity());
        productDTO.setCategoryId(product.getCategory().getCategoryId());
        return productDTO;
    }

    public static Product mapToProduct(ProductDTO productDTO) {
        Product product = new Product();
        product.setProductId(productDTO.getProductId());
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setStockQuantity(productDTO.getStockQuantity());
        //Fetch the Category from the database
        Category category = categoryService.getCategoryById(productDTO.getCategoryId());
        product.setCategory(category);
        return product;
    }

}
