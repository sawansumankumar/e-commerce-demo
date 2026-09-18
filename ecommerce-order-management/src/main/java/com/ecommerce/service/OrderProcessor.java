package com.ecommerce.service;

import com.ecommerce.exception.InsufficientStockException;
import com.ecommerce.exception.OrderNotFoundException;
import com.ecommerce.model.Order;
import com.ecommerce.model.OrderItem;
import com.ecommerce.repository.InventoryRepository;
import com.ecommerce.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderProcessor {

    private OrderRepository orderRepository;
    private InventoryRepository inventoryRepository;

    public OrderProcessor(OrderRepository orderRepository, InventoryRepository inventoryRepository)
    {
        this.orderRepository = orderRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public void processOrder(Long orderId)
    {
        Order order = orderRepository.findById(orderId).orElseThrow(()-> new OrderNotFoundException("Order not found with id: "+ orderId));
        for(OrderItem item : order.getItems())
        {
            int updatedRow = inventoryRepository.decreaseStock(item.getProduct().getId(), item.getQuantity() );
            if(updatedRow == 0)
            {
                throw new InsufficientStockException("Insufficient stock for product id: "
                        + item.getProduct().getId());
            }
        }

    }
}
