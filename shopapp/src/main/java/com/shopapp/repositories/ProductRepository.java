package com.shopapp.repositories;

import com.shopapp.models.Category;
import com.shopapp.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Long> {
    boolean existsByName(String name);
    Page<Product> findAll(Pageable pageable); //phân trang

    @Query("SELECT p FROM Product p WHERE " +
        "(:category_id IS NULL OR :category_id = 0 OR p.category.id = :category_id) " +
        "AND (:keyword IS NULL OR :keyword = '' OR p.name LIKE %:keyword% OR p.description LIKE %:keyword%)")
    Page<Product> searchProducts(
            @Param("category_id") Long categoryId,
            @Param("keyword") String keyword,
            Pageable pageable);
    @Query("SELECT p FROM Product p WHERE p.id IN :productIds")
    List<Product> findProductByIds(@Param("productIds") List<Long> productIds);
}
