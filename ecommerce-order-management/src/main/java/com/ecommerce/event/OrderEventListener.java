package com.ecommerce.event;

import com.ecommerce.model.OrderStatus;
import com.ecommerce.service.OrderProcessor;
import com.ecommerce.service.OrderStatusService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

@Component
public class OrderEventListener {

    private final ExecutorService orderExecutorService;
    private final OrderProcessor orderProcessor;
    private final OrderStatusService orderStatusService;
    private static final Logger log =
            LoggerFactory.getLogger(OrderEventListener.class);

    public OrderEventListener(
            ExecutorService orderExecutorService,
            OrderProcessor orderProcessor,
            OrderStatusService orderStatusService)
    {
        this.orderExecutorService = orderExecutorService;
        this.orderProcessor = orderProcessor;
        this.orderStatusService = orderStatusService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderSubmitted(OrderSubmittedEvent event)
    {
        Long orderId = event.getOrderId();

        orderExecutorService.submit(()->
        {
            try
            {
                orderProcessor.processOrder(orderId);
                orderStatusService.updateStatus(orderId, OrderStatus.CONFIRMED);
                log.info("Order {} processed successfully and confirmed", orderId);
            }

            catch (Exception ex)
            {
                log.error("Order {} processing failed", orderId, ex);
                orderStatusService.updateStatus(orderId, OrderStatus.FAILED);

            }

        });
    }
}
