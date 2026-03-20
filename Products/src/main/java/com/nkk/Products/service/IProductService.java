package com.nkk.Products.service;

import com.nkk.Products.dto.ProductDTO;
import com.nkk.Products.entity.Product;
import com.nkk.Products.exception.ConcurrencyException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface IProductService {

    /**
     *
     * @param page
     * @param size
     * @return
     */
    CompletableFuture<List<ProductDTO>> getAllProductsAsync(int page, int size);

    /**
     *
     * @param id
     * @return
     */
    ProductDTO getProductById(Long id);

    /**
     *
     * @param id
     * @return
     */
    Integer getProductByStock(Long id);
        /**
         *
         * @param productDTO
         * @return
         *
         */
    ProductDTO addProduct(ProductDTO productDTO);

    /**
     *
     * @param id
     * @param productDTO
     * @return
     *
     */
    ProductDTO updateProduct(Long id, ProductDTO productDTO) throws ConcurrencyException;

    /**
     *
     * @param id
     *
     */
    void deleteProduct(Long id);
    ProductDTO updateProductStock(ProductDTO productDTO) throws ConcurrencyException;
    CompletableFuture<ProductDTO> updateProductStockAsync(ProductDTO dto);
    CompletableFuture<Void> updateProductImagesAsync(Long productId, List<String> imageUrls);
    CompletableFuture<Void> reindexProductAsync(Long productId);
    CompletableFuture<Void> notifyProductChangeAsync(Long productId);

}
