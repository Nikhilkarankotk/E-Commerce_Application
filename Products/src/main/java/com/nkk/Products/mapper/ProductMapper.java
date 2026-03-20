//package com.nkk.Products.mapper;
//
//import com.nkk.Products.dto.ProductDTO;
//import com.nkk.Products.dto.SubCategoryDTO;
//import com.nkk.Products.entity.Category;
//import com.nkk.Products.entity.Product;
//import com.nkk.Products.entity.SubCategory;
//import com.nkk.Products.service.ISubCategoryService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
//@Component
//public class ProductMapper {
//
//    private static ISubCategoryService subCategoryService;
//
//    @Autowired
//    public ProductMapper(ISubCategoryService subCategoryService) {
//        ProductMapper.subCategoryService = subCategoryService;
//    }
//
//
//    public static ProductDTO mapToProductDTO(Product product) {
//        ProductDTO productDTO = new ProductDTO();productDTO.setProductId(product.getProductId());
//        productDTO.setName(product.getName());
//        productDTO.setDescription(product.getDescription());
//        productDTO.setPrice(product.getPrice());
//        productDTO.setStockQuantity(product.getStockQuantity());
//        if (product.getSubCategory() != null) {
//            SubCategory subCategory = product.getSubCategory();
//            productDTO.setSubCategoryId(subCategory.getSubCategoryId());
//            productDTO.setSubCategoryName(subCategory.getSubCategoryName());
//
//            if (subCategory.getCategory() != null) {
//                Category category = subCategory.getCategory();
//                productDTO.setSubCategoryId(category.getCategoryId());
//                productDTO.setSubCategoryName(category.getCategoryName());
//            }
//        }
//        return productDTO;
//    }
//
//    public static Product mapToProduct(ProductDTO productDTO) {
//        Product product = new Product();
//        product.setProductId(productDTO.getProductId());
//        product.setName(productDTO.getName());
//        product.setDescription(productDTO.getDescription());
//        product.setPrice(productDTO.getPrice());
//        product.setStockQuantity(productDTO.getStockQuantity());
//        //Fetch the Category from the database
//        Category category = new Category();
////        List<SubCategoryDTO> subCategory = subCategoryService.getSubCategoriesByCategoryId(category.getCategoryId());
////        product.setSubCategory(subCategory);
//        List<SubCategoryDTO> subCategories = subCategoryService.getSubCategoriesByCategoryId(category.getCategoryId());
//        if (!subCategories.isEmpty()) {
//           SubCategoryDTO subCategory = subCategories.get(0);
//            product.setSubCategory(subCategory);
//        }
//        return product;
//    }
//
//}
package com.nkk.Products.mapper;

import com.nkk.Products.dto.ProductDTO;
import com.nkk.Products.dto.SubCategoryDTO;
import com.nkk.Products.entity.Category;
import com.nkk.Products.entity.Product;
import com.nkk.Products.entity.SubCategory;
import com.nkk.Products.exception.ResourceNotFoundException;
import com.nkk.Products.repository.SubCategoryRepository;
import com.nkk.Products.service.ISubCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductMapper {

    private final SubCategoryRepository subCategoryRepository;

    public ProductMapper(SubCategoryRepository subCategoryRepository) {
        this.subCategoryRepository = subCategoryRepository;
    }

    public static ProductDTO mapToProductDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductId(product.getProductId());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setPrice(product.getPrice());
        productDTO.setStockQuantity(product.getStockQuantity());

        if (product.getSubCategory() != null) {
            SubCategory subCategory = product.getSubCategory();
            productDTO.setSubCategoryId(subCategory.getSubCategoryId());
            productDTO.setSubCategoryName(subCategory.getSubCategoryName());
        }

        return productDTO;
    }

    public Product mapToProduct(ProductDTO productDTO) {
        Product product = new Product();
        product.setProductId(productDTO.getProductId());
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setStockQuantity(productDTO.getStockQuantity());

        if(productDTO.getSubCategoryId() != null) {
            SubCategory subCategory = subCategoryRepository.findById(productDTO.getSubCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("SubCategory","ID", + productDTO.getSubCategoryId()));
            product.setSubCategory(subCategory);
        }
        return product;
    }
}