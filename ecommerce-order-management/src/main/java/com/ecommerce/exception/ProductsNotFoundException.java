package com.ecommerce.exception;

public class ProductsNotFoundException extends RuntimeException{

    public ProductsNotFoundException(String message)
    {
        super(message);
    }
}
