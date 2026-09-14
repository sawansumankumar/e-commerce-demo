package com.ecommerce.dto.request;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateInventoryRequest {


    @NotNull
    @PositiveOrZero
    private Integer quantity;
}
