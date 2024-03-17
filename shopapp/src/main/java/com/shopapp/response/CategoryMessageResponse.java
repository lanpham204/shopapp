package com.shopapp.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryMessageResponse {
    @JsonProperty("message")
    private String message;
}
