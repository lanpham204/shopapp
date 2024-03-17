package com.shopapp.services;

import com.shopapp.exception.DataNotFoundException;
import com.shopapp.exception.InvalidParamException;
import com.shopapp.models.Category;
import com.shopapp.models.Product;
import com.shopapp.models.ProductImage;
import com.shopapp.response.ProductDetailResponse;
import com.shopapp.response.ProductImageResponse;
import com.shopapp.response.ProductResponse;
import com.shopapp.repositories.CategoryRepository;
import com.shopapp.repositories.ProductImageRepository;
import com.shopapp.repositories.ProductRepository;
import com.shopapp.dtos.ProductDTO;
import com.shopapp.dtos.ProductImageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    @Override
    public Product create(ProductDTO productDTO) throws DataNotFoundException {
        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find category with id " + productDTO.getCategoryId()));
        Product newProduct = Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .thumbnail(productDTO.getThumbnail())
                .category(category)
                .description(productDTO.getDescription())
        .build();
        return productRepository.save(newProduct);
    }

    @Override
    public Product getById(long id) throws DataNotFoundException {
        return productRepository.findById(id).orElseThrow(()
                -> new DataNotFoundException("Cannot find product with id "+id));
    }

    @Override
    public ProductDetailResponse getDetailById(long id) throws DataNotFoundException {
        Product product = productRepository.findById(id).orElseThrow(()
                -> new DataNotFoundException("Cannot find product with id " + id));
        List<ProductImageResponse> productImages = productImageRepository.findByProductId(product.getId())
                .stream().map(productImage -> ProductImageResponse.builder()
                        .id(productImage.getId())
                        .imageUrl(productImage.getImageUrl())
                        .build()).toList();
        ProductDetailResponse productResponse = ProductDetailResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .thumbnail(product.getThumbnail())
                .description(product.getDescription())
                .categoryId(product.getCategory().getId())
                .productImages(productImages)
                .build();
        productResponse.setCreatedAt(product.getCreatedAt());
        productResponse.setUpdatedAt(product.getUpdatedAt());
        return productResponse;
    }
    @Override
    public List<ProductDetailResponse> getProductDetailByIds(List<Long> productIds) {
        List<ProductDetailResponse> products = productRepository.findProductByIds(productIds).stream()
                .map(product -> {
                    List<ProductImageResponse> productImages = productImageRepository.findByProductId(product.getId())
                            .stream().map(productImage -> ProductImageResponse.builder()
                                    .id(productImage.getId())
                                    .imageUrl(productImage.getImageUrl())
                                    .build()).toList();
                    ProductDetailResponse productResponse = ProductDetailResponse.builder()
                            .id(product.getId())
                            .name(product.getName())
                            .price(product.getPrice())
                            .thumbnail(product.getThumbnail())
                            .description(product.getDescription())
                            .categoryId(product.getCategory().getId())
                            .productImages(productImages)
                            .build();
                    productResponse.setCreatedAt(product.getCreatedAt());
                    productResponse.setUpdatedAt(product.getUpdatedAt());
                    return productResponse;
                }).toList();
        return products;
    }

    @Override
    public Page<ProductResponse> getAll(Long categoryId,String keyword,PageRequest pageRequest) {
       return productRepository.searchProducts(categoryId,keyword,pageRequest)
                .map(ProductResponse::fromProduct);
    }

    @Override
    @Transactional
    public ProductResponse update(long id, ProductDTO productDTO) throws DataNotFoundException {
        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find category with id " +productDTO.getCategoryId()));
        Product existingProduct = getById(id);
        if(existingProduct != null) {
           existingProduct.setName(productDTO.getName());
           existingProduct.setPrice(productDTO.getPrice());
           existingProduct.setCategory(category);
           existingProduct.setThumbnail(productDTO.getThumbnail());
           existingProduct.setDescription(productDTO.getDescription());
            productRepository.save(existingProduct);
            return ProductResponse.fromProduct(existingProduct);
        }
        return null;
    }

    @Override
    @Transactional
    public void delete(long id) {
        Optional<Product> existingProduct = productRepository.findById(id);
        if(existingProduct.isPresent()) {
            productRepository.deleteById(id);
        }
    }

    @Override
    public boolean existsByName(String name) {
        return productRepository.existsByName(name);
    }
    @Override
    public ProductImage createProductImage(Long productId, ProductImageDTO productImageDTO) throws DataNotFoundException, InvalidParamException {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find product with id " + productImageDTO.getProductId()));
        ProductImage productImage = ProductImage.builder()
                .product(existingProduct)
                .imageUrl(productImageDTO.getImageUrl()).build();
        int size = productImageRepository.findByProductId(productId).size();
        if(size>= ProductImage.MAXIMUM_IMAGES_SIZE) {
            throw new InvalidParamException("Number of image must be <= 5");
        }
        return productImageRepository.save(productImage);
    }
}
