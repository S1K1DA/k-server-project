package com.example.k_server_project.domain.payment.service;

import com.example.k_server_project.common.error.ErrorEnum;
import com.example.k_server_project.common.exception.ServiceErrorException;
import com.example.k_server_project.common.external.portone.PortOneClient;
import com.example.k_server_project.common.external.portone.PortOnePaymentResponse;
import com.example.k_server_project.domain.payment.dto.request.PaymentRequest;
import com.example.k_server_project.domain.payment.entity.Payment;
import com.example.k_server_project.domain.payment.repository.PaymentRepository;
import com.example.k_server_project.domain.user.entity.User;
import com.example.k_server_project.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PortOneClient portOneClient;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private UserService userService;

    private PaymentRequest request;
    private PortOnePaymentResponse portOneResponse;
    private User user;

    @BeforeEach
    void setUp() {
        request = new PaymentRequest("test-payment-id", 10000L);

        PortOnePaymentResponse.Amount amount = mock(PortOnePaymentResponse.Amount.class);
        given(amount.getTotal()).willReturn(10000L);

        portOneResponse = mock(PortOnePaymentResponse.class);
        given(portOneResponse.getStatus()).willReturn("PAID");
        given(portOneResponse.getAmount()).willReturn(amount);

        user = User.createUser("테스트유저");
    }

    @Test
    void 정상_결제_검증_후_포인트_충전_성공() {
        // given
        given(paymentRepository.existsByImpUid("test-payment-id")).willReturn(false);
        given(portOneClient.getPayment(any(), any())).willReturn(portOneResponse);
        given(paymentRepository.save(any())).willReturn(mock(Payment.class));
        given(userService.chargePoint(any(), any())).willReturn(user);

        // when
        var response = paymentService.verifyAndChargePoint(1L, request);

        // then
        assertThat(response).isNotNull();
        verify(userService, times(1)).chargePoint(1L, 10000L);
        verify(paymentRepository, times(1)).save(any());
    }

    @Test
    void 중복_결제_요청시_예외_발생() {
        // given
        given(paymentRepository.existsByImpUid("test-payment-id")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> paymentService.verifyAndChargePoint(1L, request))
                .isInstanceOf(ServiceErrorException.class)
                .hasMessage(ErrorEnum.ERR_DUPLICATE_PAYMENT.getMessage());

        verify(portOneClient, never()).getPayment(any(), any());
        verify(userService, never()).chargePoint(any(), any());
    }

    @Test
    void 결제_금액_불일치시_예외_발생() {
        // given
        PaymentRequest wrongAmountRequest = new PaymentRequest("test-payment-id", 20000L); // 금액 다름!
        given(paymentRepository.existsByImpUid("test-payment-id")).willReturn(false);
        given(portOneClient.getPayment(any(), any())).willReturn(portOneResponse);

        // when & then
        assertThatThrownBy(() -> paymentService.verifyAndChargePoint(1L, wrongAmountRequest))
                .isInstanceOf(ServiceErrorException.class)
                .hasMessage(ErrorEnum.ERR_PAYMENT_AMOUNT_MISMATCH.getMessage());

        verify(userService, never()).chargePoint(any(), any());
    }

    @Test
    void 결제_상태가_PAID_아닐때_예외_발생() {
        // given
        given(paymentRepository.existsByImpUid("test-payment-id")).willReturn(false);
        given(portOneResponse.getStatus()).willReturn("FAILED"); // PAID 아님!
        given(portOneClient.getPayment(any(), any())).willReturn(portOneResponse);

        // when & then
        assertThatThrownBy(() -> paymentService.verifyAndChargePoint(1L, request))
                .isInstanceOf(ServiceErrorException.class)
                .hasMessage(ErrorEnum.ERR_PAYMENT_FAILED.getMessage());

        verify(userService, never()).chargePoint(any(), any());
    }
}