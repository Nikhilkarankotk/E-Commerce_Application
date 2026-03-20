package com.nkk.Products.controller;

import com.nkk.Products.dto.ProductDTO;
import com.nkk.Products.exception.ConcurrencyException;
import com.nkk.Products.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private IProductService productService;

//    @GetMapping
//    public List<ProductDTO> getAllProducts() {
//        return productService.getAllProducts();
//    }

//    @GetMapping
//    public CompletableFuture<ResponseEntity<?>> getAllProductsAsync(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "50") int size,
//            @RequestParam(defaultValue = "false") boolean enrich) {
//
//        // page/size/enrich are primitives and have safe defaults
//        return productService.getAllProductsAsync(page, size, enrich)
//                .thenApply(list -> ResponseEntity.ok().body(list));
//    }
    /**
     * Non-blocking endpoint — returns a CompletableFuture so servlet thread is freed.
     */
    @GetMapping()
    public CompletableFuture<List<ProductDTO>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        return productService.getAllProductsAsync(page, size);
    }

    @GetMapping("/{id}")
    public ProductDTO getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @GetMapping("/stock/{id}")
    public Integer getProductByStock(@PathVariable Long id) {
        return productService.getProductByStock(id);
    }
    @PostMapping("/create")
    public ProductDTO addProduct(@RequestBody ProductDTO productDTO) {
        return productService.addProduct(productDTO);

    }

    @PutMapping("/{id}")
    public ProductDTO updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) throws ConcurrencyException {
        return productService.updateProduct(id, productDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

    // Add this new endpoint for updating stock
    @PutMapping("/stock")
    public CompletableFuture<ProductDTO> updateProductStock(@RequestBody ProductDTO productDTO){
        return productService.updateProductStockAsync(productDTO);
    }
}
   