package com.shopapp.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.shopapp.models.OrderDetail;
import com.shopapp.models.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {
    private Long id;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty( "fullname")
    private String fullName;

    @JsonProperty( "email")
    private String email;

    @JsonProperty( "phone_number")
    private String phoneNumber;

    @JsonProperty( "address")
    private String address;

    @JsonProperty( "note")
    private String note;
    @JsonProperty( "order_date")
    private LocalDateTime orderDate;

    @JsonProperty( "status")
    private String status;

    @JsonProperty( "total_money")
    private Float totalMoney;

    @JsonProperty( "shipping_method")
    private String shippingMethod;

    @JsonProperty( "shipping_address")
    private String shippingAddress;

    @JsonProperty( "shipping_date")
    private LocalDate shippingDate;

    @JsonProperty( "tracking_number")
    private String trackingNumber;

    @JsonProperty( "payment_method")
    private String paymentMethod;

    @JsonProperty( "active")
    private Boolean active;

    @JsonProperty("order_details")
    private List<OrderDetailResponse> orderDetails;
}
