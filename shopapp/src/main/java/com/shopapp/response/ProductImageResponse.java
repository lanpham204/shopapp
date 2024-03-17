package com.shopapp.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductImageResponse {
    private Long id;
   @JsonProperty("image_url")
    private String imageUrl;

}
