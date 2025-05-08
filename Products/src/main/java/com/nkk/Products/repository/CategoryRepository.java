package com.nkk.Products.repository;

import com.nkk.Products.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long>{
    boolean existsByCategoryName(String name);
}
