package com.ecommerce.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryResponse {

    private Long productId;
    private int quantity;
}
