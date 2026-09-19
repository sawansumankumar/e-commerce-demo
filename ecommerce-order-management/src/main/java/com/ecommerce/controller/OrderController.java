package com.ecommerce.controller;


import com.ecommerce.dto.request.CreateOrderRequest;
import com.ecommerce.dto.request.UpdateOrderStatusRequest;
import com.ecommerce.dto.response.OrderResponse;
import com.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService)
    {
        this.orderService = orderService;
    }

   @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request)
   {
       OrderResponse response = orderService.createOrder(request);
       return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);

   }

   @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id)
   {
       OrderResponse response = orderService.getOrderById(id);
       return ResponseEntity.ok(response);
   }

   @GetMapping("/my-orders")
    public ResponseEntity<List<OrderResponse>> getMyOrders()
   {
       List<OrderResponse> responses = orderService.getMyOrders();
       return ResponseEntity.ok(responses);

   }

   @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(@PathVariable Long id, @Valid @RequestBody UpdateOrderStatusRequest request)
   {
       OrderResponse response = orderService.updateOrderStatus(id, request);
       return ResponseEntity.ok(response);
   }

}
