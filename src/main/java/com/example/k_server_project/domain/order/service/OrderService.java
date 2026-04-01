package com.example.k_server_project.domain.order.service;

import com.example.k_server_project.common.error.ErrorEnum;
import com.example.k_server_project.common.exception.ServiceErrorException;
import com.example.k_server_project.domain.menu.entity.Menu;
import com.example.k_server_project.domain.menu.repository.MenuRepository;
import com.example.k_server_project.domain.order.dto.request.OrderRequest;
import com.example.k_server_project.domain.order.dto.response.OrderResponse;
import com.example.k_server_project.domain.order.entity.Order;
import com.example.k_server_project.domain.order.event.OrderEvent;
import com.example.k_server_project.domain.order.kafka.OrderProducer;
import com.example.k_server_project.domain.order.repository.OrderRepository;
import com.example.k_server_project.domain.user.entity.User;
import com.example.k_server_project.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;
    private final OrderProducer orderProducer;

    // 커피 주문/ 포인트 결제
    @Transactional
    public OrderResponse createOrder(Long userId, OrderRequest request) {

        // 메뉴 조회
        Menu menu = menuRepository.findById(request.getMenuId())
                .orElseThrow(() -> new ServiceErrorException(ErrorEnum.ERR_NOT_FOUND_MENU));

        // 유저 조회 (비관적 락)
        User user = userRepository.findByIdWithLock(userId)
                .orElseThrow(() -> new ServiceErrorException(ErrorEnum.ERR_NOT_FOUND_USER));

        // 포인트 차감
        user.deductPoint(menu.getPrice());

        // 주문 저장
        Order order = Order.createOrder(userId, menu.getId(), menu.getPrice());
        orderRepository.save(order);

        // Kafka 이벤트 발행
        OrderEvent event = new OrderEvent(userId, menu.getId(), menu.getPrice());
        orderProducer.send(event);

        log.info("[Order] 주문 완료 - userId={}, menuId={}, totalPrice={}",
                userId, menu.getId(), menu.getPrice());

        return new OrderResponse(order.getId(), userId, menu.getId(), menu.getPrice(), user.getPoint());
    }
}