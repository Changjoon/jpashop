package com.jpabook.jpashop;

import com.jpabook.jpashop.domain.*;
import com.jpabook.jpashop.domain.item.Item;
import com.jpabook.jpashop.service.OrderConsumer;
import com.jpabook.jpashop.service.OrderProducer;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=localhost:29092",
        "spring.kafka.consumer.group-id=test-consumer-group"
})
class OrderKafkaTest {

    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private OrderProducer orderProducer;

    @MockBean
    private OrderConsumer orderConsumer;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private Member member;

    @Mock
    private Delivery delivery;

    @Mock
    private OrderItem orderItem;

    private static final String TOPIC = "orders";

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OrderKafkaTest.class);

    @Test
    void testOrderMessageFlow() {
        // Given
        when(member.getName()).thenReturn("Test Member");
        when(delivery.getAddress()).thenReturn(new Address("city", "street", "zipcode"));
        when(orderItem.getItem()).thenReturn(new Item("Test Item", 1000, 10));

        Order order = Order.createOrder(member, delivery, orderItem);
        order.setStatus(OrderStatus.NEW);
        order.setId(100L);

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> receivedMessage = new AtomicReference<>();

        doAnswer(invocation -> {
            String message = invocation.getArgument(0);
            receivedMessage.set(message);
            latch.countDown();
            return null;
        }).when(orderConsumer).consume(anyString());

        // When
        orderProducer.sendOrder(order);

        // Then
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            assertNotNull(receivedMessage.get());
            assertTrue(receivedMessage.get().contains("Test Member"));
            assertTrue(receivedMessage.get().contains("Test Item"));
            assertTrue(receivedMessage.get().contains("\"status\":\"NEW\""));
        });

        verify(orderConsumer, timeout(5000)).consume(anyString());
    }
}