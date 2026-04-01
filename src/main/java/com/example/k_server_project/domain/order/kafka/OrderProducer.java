package com.example.k_server_project.domain.order.kafka;

import com.example.k_server_project.domain.order.event.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderProducer {

    private static final String TOPIC = "order-completed";

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    // 주문 정보 카프카 전달
    public void send(OrderEvent event) {
        kafkaTemplate.send(TOPIC, event);
        log.info("[OrderProducer] 주문 이벤트 발행 - userId={}, menuId={}, totalPrice={}",
                event.getUserId(), event.getMenuId(), event.getTotalPrice());
    }
}