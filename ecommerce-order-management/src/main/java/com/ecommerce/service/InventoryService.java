package com.ecommerce.service;


import com.ecommerce.dto.response.InventoryResponse;
import com.ecommerce.dto.request.UpdateInventoryRequest;
import com.ecommerce.exception.InventoryNotFoundException;
import com.ecommerce.model.Inventory;
import com.ecommerce.repository.InventoryRepository;
import org.springframework.stereotype.Service;


@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository)
    {
        this.inventoryRepository = inventoryRepository;
    }

    public InventoryResponse getInventoryByProductId(Long productId)
    {
        Inventory inventory = inventoryRepository.findByProductId(productId).orElseThrow(()->new InventoryNotFoundException
                ("Inventory not found product for id " + productId));

        return mapToResponse(inventory);
    }

    private InventoryResponse mapToResponse(Inventory inventory)
    {
        InventoryResponse response = new InventoryResponse();
        response.setQuantity(inventory.getQuantity());
        response.setProductId(inventory.getProduct().getId());

        return response;
    }

    public InventoryResponse updateInventory(Long productId, UpdateInventoryRequest request)
    {
        Inventory existingInventory = inventoryRepository.findByProductId(productId).
                orElseThrow(()-> new InventoryNotFoundException("No Inventory found for product id " + productId));

        existingInventory.setQuantity(request.getQuantity());
        Inventory inventory = inventoryRepository.save(existingInventory);
        InventoryResponse response = mapToResponse(inventory);
        return response;

    }
}
