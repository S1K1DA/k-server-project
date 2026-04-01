package com.example.k_server_project.domain.order.kafka;

import com.example.k_server_project.domain.order.event.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderConsumer {

    // 토픽에서 메시지 받기
    @KafkaListener(topics = "order-completed", groupId = "coffee-group")
    public void consume(OrderEvent event) {

        log.info("[OrderConsumer] 주문 이벤트 수신 - userId={}, menuId={}, totalPrice={}",
                event.getUserId(), event.getMenuId(), event.getTotalPrice());


    }
}