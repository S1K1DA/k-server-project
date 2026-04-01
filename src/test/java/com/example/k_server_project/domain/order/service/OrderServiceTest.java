package com.example.k_server_project.domain.order.service;

import com.example.k_server_project.common.error.ErrorEnum;
import com.example.k_server_project.common.exception.ServiceErrorException;
import com.example.k_server_project.domain.menu.entity.Menu;
import com.example.k_server_project.domain.menu.repository.MenuRepository;
import com.example.k_server_project.domain.order.dto.request.OrderRequest;
import com.example.k_server_project.domain.order.entity.Order;
import com.example.k_server_project.domain.order.kafka.OrderProducer;
import com.example.k_server_project.domain.order.repository.OrderRepository;
import com.example.k_server_project.domain.user.entity.User;
import com.example.k_server_project.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderProducer orderProducer;

    private User user;
    private Menu menu;
    private OrderRequest request;

    @BeforeEach
    void setUp() {
        user = User.createUser("테스트유저");
        user.chargePoint(10000L); // 포인트 10000P 충전

        menu = Menu.createMenu("아메리카노", 4500L);
        request = new OrderRequest(1L);
    }

    @Test
    void 정상_주문_성공() {
        // given
        given(menuRepository.findById(1L)).willReturn(Optional.of(menu));
        given(userRepository.findByIdWithLock(1L)).willReturn(Optional.of(user));
        given(orderRepository.save(any())).willReturn(mock(Order.class));

        // when
        var response = orderService.createOrder(1L, request);

        // then
        assertThat(response).isNotNull();
        verify(orderProducer, times(1)).send(any());
        verify(orderRepository, times(1)).save(any());
    }

    @Test
    void 메뉴_없을때_예외_발생() {
        // given
        given(menuRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderService.createOrder(1L, request))
                .isInstanceOf(ServiceErrorException.class)
                .hasMessage(ErrorEnum.ERR_NOT_FOUND_MENU.getMessage());

        verify(orderProducer, never()).send(any());
    }

    @Test
    void 유저_없을때_예외_발생() {
        // given
        given(menuRepository.findById(1L)).willReturn(Optional.of(menu));
        given(userRepository.findByIdWithLock(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderService.createOrder(1L, request))
                .isInstanceOf(ServiceErrorException.class)
                .hasMessage(ErrorEnum.ERR_NOT_FOUND_USER.getMessage());

        verify(orderProducer, never()).send(any());
    }

    @Test
    void 포인트_부족할때_예외_발생() {
        // given 포인트 없음
        User poorUser = User.createUser("포인트없는유저");

        given(menuRepository.findById(1L)).willReturn(Optional.of(menu));
        given(userRepository.findByIdWithLock(1L)).willReturn(Optional.of(poorUser));

        // when & then
        assertThatThrownBy(() -> orderService.createOrder(1L, request))
                .isInstanceOf(ServiceErrorException.class)
                .hasMessage(ErrorEnum.ERR_NOT_ENOUGH_POINT.getMessage());

        verify(orderProducer, never()).send(any());
    }
}