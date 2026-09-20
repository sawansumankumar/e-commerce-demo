package com.ecommerce.controller;


import com.ecommerce.dto.response.AdminInventoryResponse;
import com.ecommerce.dto.response.AdminUserResponse;
import com.ecommerce.dto.response.OrderResponse;
import com.ecommerce.service.InventoryService;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController
{
    private final OrderService orderService;
    private final UserService userService;
    private final InventoryService inventoryService;

    public AdminController(OrderService orderService, UserService userService, InventoryService inventoryService)
    {
        this.orderService = orderService;
        this.userService = userService;
        this.inventoryService =inventoryService;
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

    @GetMapping("/inventory")
    public ResponseEntity<Page<AdminInventoryResponse>> getAllInventory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<AdminInventoryResponse> inventory =
                inventoryService.getAllInventoryForAdmin(page, size);

        return ResponseEntity.ok(inventory);
    }

}
