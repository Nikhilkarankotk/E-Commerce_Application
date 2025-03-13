package com.nkk.Products.service.impl;

import com.nkk.Products.dto.ProductDTO;
import com.nkk.Products.entity.Category;
import com.nkk.Products.entity.Product;
import com.nkk.Products.mapper.ProductMapper;
import com.nkk.Products.repository.ProductRepository;
import com.nkk.Products.service.ICategoryService;
import com.nkk.Products.service.IProductService;
import jakarta.persistence.EntityNotFoundException;
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
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
        return productMapper.mapToProductDTO(product);
    }
    @Transactional
    public ProductDTO addProduct(ProductDTO productDTO) {
        Product product = productMapper.mapToProduct(productDTO);
        //fetch the category from the database
        Category category = categoryService.getCategoryById(productDTO.getCategoryId());
        if (category == null) {
            throw new RuntimeException("Category not found with id: " + productDTO.getCategoryId());
        }
        product.setCategory(category);
        validateProduct(product);
        Product savedProduct = productRepository.save(product);
        return productMapper.mapToProductDTO(savedProduct);
    }
    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
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
        if (product.getPrice() <= 0) {
            throw new RuntimeException("Price must be greater than 0");
        }
        if (product.getStockQuantity() < 0) {
            throw new RuntimeException("Stock quantity cannot be negative");
        }
        if (product.getName() == null || product.getName().isEmpty()) {
            throw new RuntimeException("Product name is required");
        }
        if (product.getCategory() == null) {
            throw new RuntimeException("Product category is required");
        }
    }

    @Transactional
    public ProductDTO updateProductStock(ProductDTO productDTO) {
        // Fetch the product by ID
        Product product = productRepository.findById(productDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productDTO.getProductId()));
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
