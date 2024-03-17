package com.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailDTO {
    @JsonProperty("order_id")
    @Min(value = 1, message = "User's ID must be > 0")
    private Long orderId;

    @JsonProperty("product_id")
    @Min(value = 1, message = "User's ID must be > 0")
    private Long productId;

    @Min(value =  0, message = "Price must be > 0")
    private Float price;

    @JsonProperty("number_of_products")
    @Min(value =  1, message = "Number Of Products must be > 1")
    private int numberOfProducts;

    @Min(value =  0, message = "Total Money must be >= 0")
    @JsonProperty("total_money")
    private Float totalMoney;

    @JsonProperty("color")
    private String color;


}
