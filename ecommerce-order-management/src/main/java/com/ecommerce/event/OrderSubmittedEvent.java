package com.ecommerce.event;

public class OrderSubmittedEvent
{
    private final Long orderId;

    public OrderSubmittedEvent(Long orderId)
    {
        this.orderId = orderId;
    }

    public Long getOrderId()
    {
        return orderId;
    }
}
