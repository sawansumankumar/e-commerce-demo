package com.ecommerce.controller;


import com.ecommerce.dto.response.AdminUserResponse;
import com.ecommerce.dto.response.OrderResponse;
import com.ecommerce.dto.response.UserResponse;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController
{
    private final OrderService orderService;
    private final UserService userService;

    public AdminController(OrderService orderService, UserService userService)
    {
        this.orderService = orderService;
        this.userService = userService;
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

    @GetMapping("/users")
    public ResponseEntity<List<AdminUserResponse>> getAllUsers()
    {
        List<AdminUserResponse> responses = userService.getAllUsers();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<AdminUserResponse> getUserById(@PathVariable Long id)
    {
        AdminUserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/users/{userId}/deactivate")
    public ResponseEntity<Void> deactivateUserByAdmin(@PathVariable Long userId)
    {
        userService.deactivateUserByAdmin(userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/users/{userId}/activate")
    public ResponseEntity<Void> activateUserByAdmin(@PathVariable Long userId)
    {
        userService.activateUserByAdmin(userId);
        return ResponseEntity.noContent().build();
    }

}
