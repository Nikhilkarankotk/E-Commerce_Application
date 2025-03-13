package com.nkk.Products.service;

import com.nkk.Products.dto.ProductDTO;
import com.nkk.Products.entity.Product;

import java.util.List;

public interface IProductService {

    /**
     *
     * @return
     */
    List<ProductDTO> getAllProducts();

    /**
     *
     * @param id
     * @return
     */
    ProductDTO getProductById(Long id);

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
    ProductDTO updateProduct(Long id, ProductDTO productDTO);

    /**
     *
     * @param id
     *
     */
    void deleteProduct(Long id);

    ProductDTO updateProductStock(ProductDTO productDTO);

}
