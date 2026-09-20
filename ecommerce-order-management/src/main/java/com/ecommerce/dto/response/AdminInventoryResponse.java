package com.ecommerce.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AdminInventoryResponse {

    private Long productId;
    private String name;
    private String sku;
    private String category;
    private BigDecimal price;
    private Integer quantity;
    private boolean active;
}
