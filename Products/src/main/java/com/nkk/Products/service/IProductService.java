package com.nkk.Products.service;//package com.nkk.Products.service;
//
//import com.nkk.Products.dto.ProductDTO;
//import com.nkk.Products.entity.Product;
//
//import java.util.List;
//
//public interface IProductService {
//
//    /**
//     *
//     * @return
//     */
//    List<ProductDTO> getAllProducts();
//
//    /**
//     *
//     * @param id
//     * @return
//     */
//    ProductDTO getProductById(Long id);
//
//    /**
//     *
//     * @param id
//     * @return
//     */
//    Integer getProductByStock(Long id);
//        /**
//         *
//         * @param productDTO
//         * @return
//         *
//         */
//    ProductDTO addProduct(ProductDTO productDTO);
//
//    /**
//     *
//     * @param id
//     * @param productDTO
//     * @return
//     *
//     */
//    ProductDTO updateProduct(Long id, ProductDTO productDTO);
//
//    /**
//     *
//     * @param id
//     *
//     */
//    void deleteProduct(Long id);
//
//    ProductDTO updateProductStock(ProductDTO productDTO);
//
//}

import com.nkk.Products.dto.ProductDTO;

import java.util.List;

public interface IProductService {
    ProductDTO updateProductStock(ProductDTO productDTO);
    List<ProductDTO> getAllProducts();
    ProductDTO addProduct(ProductDTO productDTO);
    ProductDTO getProductById(Long id);
    Integer getProductByStock(Long id);
    ProductDTO updateProduct(Long id, ProductDTO productDTO);
    void deleteProduct(Long id);
}

