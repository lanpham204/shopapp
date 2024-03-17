package com.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

@Table(name = "product_images")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductImageDTO {
    @Min(value = 1, message = "Product's Id must be > 0")
    @JsonProperty("product_id")
    private Long productId;
    @Size(min = 5, max = 200, message = "Image's url must be between 5 and 200 characters")
    @JsonProperty("image_url")
    private String imageUrl;

}
