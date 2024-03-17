package com.shopapp.services;



import com.shopapp.exception.DataNotFoundException;
import com.shopapp.exception.InvalidParamException;
import com.shopapp.models.Product;
import com.shopapp.models.ProductImage;
import com.shopapp.response.ProductDetailResponse;
import com.shopapp.dtos.ProductDTO;
import com.shopapp.dtos.ProductImageDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.shopapp.response.ProductResponse;

import java.util.List;

public interface IProductService{
    Product create(ProductDTO productDTO) throws DataNotFoundException;
    Product getById(long id) throws DataNotFoundException;
    ProductDetailResponse getDetailById(long id) throws DataNotFoundException;
    List<ProductDetailResponse> getProductDetailByIds(List<Long> productIds);
    Page<ProductResponse> getAll(Long categoryId,String keyword,PageRequest pageRequest);
    ProductResponse update(long id, ProductDTO productDTO) throws DataNotFoundException;
    void delete(long id);
    boolean existsByName(String name);
    ProductImage createProductImage(Long productId, ProductImageDTO productImageDTO)
            throws DataNotFoundException, InvalidParamException;
}
