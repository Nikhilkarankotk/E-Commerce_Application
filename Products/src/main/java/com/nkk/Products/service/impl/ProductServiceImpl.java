package com.nkk.Products.service.impl;

import com.nkk.Products.dto.ProductDTO;
import com.nkk.Products.entity.Category;
import com.nkk.Products.entity.Product;
import com.nkk.Products.exception.ResourceAlreadyExistsException;
import com.nkk.Products.exception.ResourceNotFoundException;
import com.nkk.Products.exception.PriceStockValidationException;
import com.nkk.Products.mapper.ProductMapper;
import com.nkk.Products.repository.ProductRepository;
import com.nkk.Products.service.ICategoryService;
import com.nkk.Products.service.IProductService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements IProductService {

    private final ProductRepository productRepository;
    private final ICategoryService categoryService;
    private final ProductMapper productMapper;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, ICategoryService categoryService, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.productMapper = productMapper;
    }

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductMapper::mapToProductDTO)
                .collect(Collectors.toList());
    }
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return productMapper.mapToProductDTO(product);
    }
    @Transactional
    public ProductDTO addProduct(ProductDTO productDTO) {
        Product product = productMapper.mapToProduct(productDTO);
        //fetch the category from the database
        Category category = categoryService.getCategoryById(productDTO.getCategoryId());
        if (category == null) {
            throw new ResourceNotFoundException("Category", "id", productDTO.getCategoryId());
        }
        // Check if product already exists with same name in the same category
        if (productRepository.existsByNameAndCategory_CategoryId(productDTO.getName(), productDTO.getCategoryId())) {
            throw new ResourceAlreadyExistsException("Product already exists with name: "
                    + productDTO.getName() + " in category ID: " + productDTO.getCategoryId());
        }
        product.setCategory(category);
        validateProduct(product);
        Product savedProduct = productRepository.save(product);
        return productMapper.mapToProductDTO(savedProduct);
    }
    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id ));
        Product updatedProduct = productMapper.mapToProduct(productDTO);
        existingProduct.setName(updatedProduct.getName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setStockQuantity(updatedProduct.getStockQuantity());
        existingProduct.setCategory(updatedProduct.getCategory());
        validateProduct(existingProduct);
        Product savedProduct = productRepository.save(existingProduct);
        return productMapper.mapToProductDTO(savedProduct);
    }
    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
    private void validateProduct(Product product) {
        if (product.getStockQuantity() <= 0) {
            throw new PriceStockValidationException("Stock quantity cannot be negative or zero");
        }
        if (product.getPrice() <= 0) {
            throw new PriceStockValidationException("Price must be greater than 0");
        }
        if (product.getName() == null || product.getName().trim().isEmpty() || product.getDescription() == null || product.getDescription().trim().isEmpty()) {
            throw new PriceStockValidationException("Product name is required and description is required");
        }
        if (product.getCategory() == null) {
            throw new PriceStockValidationException("Product category is required");
        }
    }

    @Transactional
    public ProductDTO updateProductStock(ProductDTO productDTO) {
        // Fetch the product by ID
        Product product = productRepository.findById(productDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productDTO.getProductId()));
        // Update the stock quantity
        int updatedStock = product.getStockQuantity() + productDTO.getStockQuantity();
        product.setStockQuantity(updatedStock);
        // Save the updated product
        Product savedProduct = productRepository.save(product);
        // Map the updated product to DTO and return
        return mapToDTO(savedProduct);
    }
    private ProductDTO mapToDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductId(product.getProductId());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setPrice(product.getPrice());
        productDTO.setStockQuantity(product.getStockQuantity());
        return productDTO;
    }
}
