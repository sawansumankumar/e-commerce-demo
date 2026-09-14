package com.ecommerce.controller;


import com.ecommerce.dto.response.OrderResponse;
import com.ecommerce.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController
{
    private final OrderService orderService;

    public AdminController(OrderService orderService)
    {
        this.orderService = orderService;
    }

    @GetMapping("/test")
    public ResponseEntity<String> testAdmin()
    {
        return ResponseEntity.ok("Admin access working well");
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getAllOrders()
    {
        List<OrderResponse> responses = orderService.getAllOrders();
        return ResponseEntity.ok(responses);
    }
}
