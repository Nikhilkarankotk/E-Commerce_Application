package com.nkk.Products.repository;

import com.nkk.Products.entity.Product;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    boolean existsByNameAndSubCategory_SubCategoryId(String name, Long subCategoryId);

    @Query("select p.productId from Product p order by p.productId")
    List<Long> findProductIds(PageRequest pageRequest);
}
