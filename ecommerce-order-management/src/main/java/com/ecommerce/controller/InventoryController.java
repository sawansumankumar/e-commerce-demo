package com.ecommerce.controller;



import com.ecommerce.dto.response.InventoryResponse;
import com.ecommerce.dto.request.UpdateInventoryRequest;
import com.ecommerce.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService)
    {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/api/inventory/product/{id}")
    public InventoryResponse getInventoryByProductId(@PathVariable Long id)
    {
        return inventoryService.getInventoryByProductId(id);
    }

    @PutMapping("/api/inventory/product/{id}")
    public ResponseEntity<InventoryResponse> updateInventory(@PathVariable Long id, @Valid @RequestBody UpdateInventoryRequest request)
    {
        InventoryResponse updateInventory = inventoryService.updateInventory(id, request);
        return ResponseEntity.ok(updateInventory);

    }
}
