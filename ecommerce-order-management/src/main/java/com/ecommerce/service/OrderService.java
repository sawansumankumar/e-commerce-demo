package com.ecommerce.service;

import com.ecommerce.dto.request.CreateOrderRequest;
import com.ecommerce.dto.request.OrderItemRequest;
import com.ecommerce.dto.request.UpdateOrderStatusRequest;
import com.ecommerce.dto.response.OrderItemResponse;
import com.ecommerce.dto.response.OrderResponse;
import com.ecommerce.exception.*;
import com.ecommerce.model.*;
import com.ecommerce.repository.InventoryRepository;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {


    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;



    public OrderService(OrderRepository orderRepository, UserRepository userRepository,
                        ProductRepository productRepository, InventoryRepository inventoryRepository)
    {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;

    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request)
    {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(
                                        "User not found with email: " + email));
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for(OrderItemRequest item : request.getItems())
        {

            Product product = productRepository.findById(item.getProductId()).orElseThrow(()->
                    new ProductNotFoundException("Product not found with id:" + item.getProductId()));

            int updatedRows = inventoryRepository.decreaseStock(
                    item.getProductId(),
                    item.getQuantity()
            );

            if (updatedRows == 0)
            {
                throw new InsufficientStockException(
                        "Insufficient stock for product id: " + item.getProductId()
                );
            }

            //inventory.setQuantity(inventory.getQuantity() - item.getQuantity());
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            totalAmount = totalAmount.add(orderItem.getSubtotal());
            orderItems.add(orderItem);

        }
        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

       /* OrderResponse response = new OrderResponse();
        response.setOrderId(savedOrder.getId());
        response.setStatus(savedOrder.getStatus());
        response.setTotalAmount(savedOrder.getTotalAmount());

        List<OrderItemResponse> itemResponses = new ArrayList<>();
        for (OrderItem orderItem : orderItems)
        {
            OrderItemResponse itemResponse = new OrderItemResponse();
            itemResponse.setProductName(orderItem.getProduct().getName());
            itemResponse.setQuantity(orderItem.getQuantity());
            itemResponse.setUnitPrice(orderItem.getUnitPrice());
            itemResponse.setSubtotal(orderItem.getSubtotal());

            itemResponses.add(itemResponse);
        }



        response.setItems(itemResponses);
        return response; */

        return mapToResponse(savedOrder);

    }

    public OrderResponse getOrderById(Long id)
    {
        Order order = orderRepository.findById(id).orElseThrow(()->
                new OrderNotFoundException("order not found with id: " + id));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().
                getAuthorities().stream().anyMatch(authority ->authority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !order.getUser().getEmail().equals(email)) {
            throw new AccessDeniedException(
                    "You are not allowed to view this order"
            );
        }

        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getId());
        response.setStatus(order.getStatus());
        response.setTotalAmount(order.getTotalAmount());

        List<OrderItemResponse> itemResponses = new ArrayList<>();
        for(OrderItem item : order.getItems())
        {
            OrderItemResponse itemResponse = new OrderItemResponse();

            itemResponse.setProductName(item.getProduct().getName());
            itemResponse.setQuantity(item.getQuantity());
            itemResponse.setUnitPrice(item.getUnitPrice());
            itemResponse.setSubtotal(item.getSubtotal());
            itemResponses.add(itemResponse);
        }

        response.setItems(itemResponses);
        return response;
    }

    public List<OrderResponse> getMyOrders()
    {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(
                                "User not found with email: " + email));

        List<Order> orders = orderRepository.findByUserId(user.getId());
        List<OrderResponse> responses = new ArrayList<>();

        for(Order order : orders)
        {

            responses.add(mapToResponse(order));
        }

        return responses;
    }

    private OrderResponse mapToResponse(Order order)
    {
        OrderResponse response = new OrderResponse();
        response.setUserID(order.getUser().getId());
        response.setOrderId(order.getId());
        response.setStatus(order.getStatus());
        response.setTotalAmount(order.getTotalAmount());

        List<OrderItemResponse> itemResponses = new ArrayList<>();
        for(OrderItem orderItem : order.getItems())
        {
            OrderItemResponse itemResponse = new OrderItemResponse();
            itemResponse.setProductName(orderItem.getProduct().getName());
            itemResponse.setQuantity(orderItem.getQuantity());
            itemResponse.setUnitPrice(orderItem.getUnitPrice());
            itemResponse.setSubtotal(orderItem.getSubtotal());

            itemResponses.add(itemResponse);
        }

        response.setItems(itemResponses);
        return response;

    }

    public OrderResponse updateOrderStatus(Long id, UpdateOrderStatusRequest request)
    {

        Order order = orderRepository.findById(id).orElseThrow(()-> new OrderNotFoundException("Order not found with id:" + id));
        if(!isValidStatusTransition(order.getStatus(), request.getStatus()))
        {
            throw new InvalidOrderStatusTransitionException(
                    "Cannot change order status from "
                            + order.getStatus()
                            + " to "
                            + request.getStatus()
            );
        }
        order.setStatus(request.getStatus());

        Order saveOrder = orderRepository.save(order);
        return mapToResponse(saveOrder);
    }

    private boolean isValidStatusTransition(OrderStatus current, OrderStatus next)
    {
            switch (current)
            {
                case PENDING:
                    return next == OrderStatus.CONFIRMED
                            || next == OrderStatus.CANCELLED;

                case CONFIRMED:
                    return next == OrderStatus.CANCELLED
                            || next == OrderStatus.SHIPPED;

                case SHIPPED:
                    return next == OrderStatus.DELIVERED;

                case DELIVERED:
                case CANCELLED:
                    return false;
            }

            return false;
    }

    public List<OrderResponse> getAllOrders() {

        List<Order> orders = orderRepository.findAll();

        List<OrderResponse> responses = new ArrayList<>();

        for (Order order : orders) {
            responses.add(mapToResponse(order));
        }

        return responses;
    }



}
