package com.jpabook.jpashop;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

// JUnit 5 imports
import com.jpabook.jpashop.domain.*;
import com.jpabook.jpashop.domain.item.Item;
import com.jpabook.jpashop.service.OrderConsumer;
import com.jpabook.jpashop.service.OrderProducer;
import org.junit.jupiter.api.Test;
import java.util.concurrent.TimeUnit;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;

@EmbeddedKafka(partitions = 1,
        brokerProperties = { "listeners=PLAINTEXT://localhost:29092", "port=29092" },
        topics = { "orders" })
@SpringBootTest
public class KafkaProducerConsumerTest {

    @Autowired
    private OrderProducer orderProducer;

    @MockBean
    private OrderConsumer orderConsumer;

    @Mock
    private Member member;

    @Mock
    private Delivery delivery;

    @Mock
    private OrderItem orderItem;

    @Test
    void testOrderMessageFlow() {
        // Prepare an Order instance
        when(member.getName()).thenReturn("Test Member");
        when(delivery.getAddress()).thenReturn(new Address("city", "street", "zipcode"));
        when(orderItem.getItem()).thenReturn(new Item("Test Item", 1000, 10));

        Order order = Order.createOrder(member, delivery, orderItem);
        order.setStatus(OrderStatus.NEW);
        order.setId(100L);
        // ... Configure testOrder

        // Send the order message
        orderProducer.sendOrder(order);

        // Mocked orderConsumer.consume() invocation
        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
        verify(orderConsumer, timeout(TimeUnit.SECONDS.toMillis(5)).times(1)).consume(valueCapture.capture());

        // Assert the received message
        String receivedMessage = valueCapture.getValue();
        assertTrue(receivedMessage != null && !receivedMessage.isEmpty());

        // ... Add more assertions to validate the content of the Order message
    }
}