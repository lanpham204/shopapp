package com.shopapp.repositories;

import com.shopapp.models.Category;
import com.shopapp.models.Product;
import com.shopapp.models.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage,Long> {
    List<ProductImage> findByProductId(Long productId);
}
