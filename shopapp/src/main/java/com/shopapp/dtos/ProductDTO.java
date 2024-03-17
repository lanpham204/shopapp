package com.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    @NotEmpty(message = "Product's name cannot be empty")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 2 characters")
    private String name;
    @Min(value =  0, message = "Price must be greater than or equal to 0")
    @Max(value =  100000000, message = "Price must be less than or equal to 100000000")
    private Float price;
    private String thumbnail;
    private String description;
    @JsonProperty("category_id")
    private Long categoryId;
}
