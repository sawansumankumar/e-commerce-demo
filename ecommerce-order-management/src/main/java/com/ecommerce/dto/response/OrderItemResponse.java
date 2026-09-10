package com.ecommerce.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderItemResponse {

    private String productName;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;

}
