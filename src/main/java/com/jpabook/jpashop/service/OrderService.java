package com.jpabook.jpashop.service;

import com.jpabook.jpashop.domain.*;
import com.jpabook.jpashop.repository.ItemRepository;
import com.jpabook.jpashop.repository.MemberRepository;
import com.jpabook.jpashop.repository.OrderRepository;
import com.jpabook.jpashop.repository.OrderSearch;
import com.jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final MemberRepository memberRepository;

    private final ItemRepository itemRepository;

    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    /**
     * 주문
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {

        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());


        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        Order order = Order.createOrder(member, delivery, orderItem);

        orderRepository.save(order);

        sendOrderToKafka(order);

        return order.getId();
    }

    private void sendOrderToKafka(Order order) {
        try {
            String orderJson = convertOrderToJson(order);
            ListenableFuture<SendResult<String, String>> future =
                    kafkaTemplate.send("orders", order.getId().toString(), orderJson);

            future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
                @Override
                public void onSuccess(SendResult<String, String> result) {
                    logger.info("Sent message=[" + orderJson +
                            "] with offset=[" + result.getRecordMetadata().offset() + "]");
                }

                @Override
                public void onFailure(Throwable ex) {
                    logger.error("Unable to send message=["
                            + orderJson + "] due to : " + ex.getMessage());
                }
            });
        } catch (Exception e) {
            logger.error("Error while sending order to Kafka", e);
            // 여기서 추가적인 에러 처리를 할 수 있습니다.
            // 예: 데이터베이스에 실패한 메시지 저장, 알림 발송 등
        }
    }

    private String convertOrderToJson(Order order) {
        // Order 객체를 JSON 문자열로 변환하는 로직
        // 여기서는 간단히 toString()을 사용했지만, 실제로는 Jackson 등의 라이브러리를 사용하는 것이 좋습니다.
        return order.toString();
    }

    @Transactional
    public void processReceivedOrder(Order receivedOrder) {
        logger.info("Processing received order: {}", receivedOrder);

        // 1. 주문이 이미 존재하는지 확인
        Order existingOrder = orderRepository.findOne(receivedOrder.getId());

        if (existingOrder != null) {
            // 2. 기존 주문이 있다면 상태 업데이트
            existingOrder.setStatus(receivedOrder.getStatus());
            logger.info("Updated existing order: {}", existingOrder);
        } else {
            // 3. 새 주문이라면 저장
            orderRepository.save(receivedOrder);
            logger.info("Saved new order: {}", receivedOrder);
        }

        // 4. 추가적인 비즈니스 로직 수행
        // 예: 재고 확인, 결제 처리, 배송 준비 등
        performAdditionalProcessing(receivedOrder);
    }

    private void performAdditionalProcessing(Order order) {
        // 여기에 추가적인 주문 처리 로직을 구현합니다.
        // 예: 재고 확인, 결제 처리, 배송 준비 등
        logger.info("Performing additional processing for order: {}", order);

        // 예시: 주문 상태에 따른 처리
        if (order.getStatus() == OrderStatus.PAID) {
            // 결제 완료된 주문 처리
            prepareForShipment(order);
        } else if (order.getStatus() == OrderStatus.SHIPPED) {
            // 배송 시작된 주문 처리
            updateShippingStatus(order);
        }
    }

    private void prepareForShipment(Order order) {
        // 배송 준비 로직
        logger.info("Preparing order for shipment: {}", order);
        // 실제 배송 준비 로직 구현...
    }

    private void updateShippingStatus(Order order) {
        // 배송 상태 업데이트 로직
        logger.info("Updating shipping status for order: {}", order);
        // 실제 배송 상태 업데이트 로직 구현...
    }

    /**
     * 주문 취소
     */
    @Transactional
    public void cancelOrder(Long orderId) {

        Order order = orderRepository.findOne(orderId);
        order.cancel();
    }

    /**
     * 주문 검색
     */
    public List<Order> findAllByString(OrderSearch orderSearch) {
        return orderRepository.findAllByString(orderSearch);
    }

    public List<Order> findOrders(OrderSearch orderSearch) {
        return orderRepository.findAllByCriteria(orderSearch);
    }
}
