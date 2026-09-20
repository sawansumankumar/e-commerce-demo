package com.ecommerce.controller;


import java.util.List;


import com.ecommerce.dto.request.CreateProductRequest;
import com.ecommerce.dto.response.ProductResponse;
import com.ecommerce.dto.request.UpdateProductRequest;
import com.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.*;


@RestController
public class ProductController
{
    private final ProductService productService;

    public ProductController(ProductService productService)
    {
        this.productService = productService;
    }


    @GetMapping("/api/products")
    public List<ProductResponse> getProducts()
    {
        return productService.getProducts();
    }


    @GetMapping("/api/products/page")
    public Page<ProductResponse> getProducts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size)
    {
        Page<ProductResponse> responses = productService.getProducts(page, size);

        return responses;
    }

    @GetMapping("/api/products/{id}")
    public ProductResponse getProductById(@PathVariable Long id)
    {
        return productService.getProductById(id);
    }

    @GetMapping(value = "/api/products/search", params = "category")
    public List<ProductResponse> getProductsByCategory(@RequestParam String category)
    {
        return productService.getProductsByCategory(category);
    }

    @GetMapping(value = "/api/products/search", params = "active")
    public List<ProductResponse> getProductByActive(@RequestParam boolean active)
    {
        return productService.getProductByActive(active);
    }

    @PostMapping("/api/products")
    public ResponseEntity<ProductResponse> createProduct( @Valid @RequestBody CreateProductRequest request)
    {

        ProductResponse savedProduct = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }



    @PutMapping("/api/products/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request)
    {
        ProductResponse updatedProduct = productService.updateProduct(id, request);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/api/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id)
    {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/products/bulk")
    public ResponseEntity<List<ProductResponse>> createProducts(@Valid @RequestBody List<@Valid CreateProductRequest> requests)
    {
        List<ProductResponse> responses = productService.createProducts(requests);
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }


}
