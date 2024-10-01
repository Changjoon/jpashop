package com.jpabook.jpashop.service;

import lombok.Setter;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpabook.jpashop.domain.Order;
import com.jpabook.jpashop.service.OrderService;

import java.io.IOException;
import java.util.function.Consumer;

@Service
public class OrderConsumer {

    private static final Logger logger = LoggerFactory.getLogger(OrderConsumer.class);
    private final ObjectMapper objectMapper;
    private final OrderService orderService;
    @Setter
    private Consumer<String> messageHandler;

    public OrderConsumer(ObjectMapper objectMapper, OrderService orderService) {
        this.objectMapper = objectMapper;
        this.orderService = orderService;
    }

    @KafkaListener(topics = "orders", groupId = "test-consumer-group")
    public void consume(String message) {
        logger.info("Received message: {}", message);

        if (messageHandler != null) {
            messageHandler.accept(message);
        }

        try {
            Order order = objectMapper.readValue(message, Order.class);
            processOrder(order);
        } catch (IOException e) {
            logger.error("Error deserializing order message: {}", e.getMessage());
        }
    }

    private void processOrder(Order order) {
        // 여기에 주문 처리 로직을 구현합니다.
        // 예를 들어, 데이터베이스에 저장하거나 다른 서비스를 호출할 수 있습니다.
        logger.info("Processing order: {}", order);
        orderService.processReceivedOrder(order);
    }
}