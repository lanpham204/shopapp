package com.shopapp.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.shopapp.models.Order;
import com.shopapp.models.OrderDetail;
import com.shopapp.models.Product;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailResponse {
    private Long id;

    @JsonProperty("order_id")
    private Long orderId;

    @JsonProperty("product")
    private Product product;

    @JsonProperty("price")
    private Float price;

    @JsonProperty("number_of_products")
    private int numberOfProducts;

    @JsonProperty("total_money")
    private Float totalMoney;

    @JsonProperty("color")
    private String color;

}
