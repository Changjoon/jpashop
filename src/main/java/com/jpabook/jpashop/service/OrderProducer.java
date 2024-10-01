package com.jpabook.jpashop.service;

import com.jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final Logger log = LoggerFactory.getLogger(OrderProducer.class);

    public void sendOrder(Order order) {
        log.info("Preparing to send order {} with content {}", order.getId().toString(), order.toString());
        kafkaTemplate.send("orders", order.getId().toString(), order.toString());
    }
}
