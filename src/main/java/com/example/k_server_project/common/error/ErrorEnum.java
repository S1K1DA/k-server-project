package com.example.k_server_project.common.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static com.example.k_server_project.common.error.ErrorMessage.*;

@Getter
public enum ErrorEnum {

    // 메뉴 관련
    ERR_NOT_FOUND_MENU(HttpStatus.NOT_FOUND, MSG_NOT_FOUND_MENU),

    // 유저 관련
    ERR_NOT_FOUND_USER(HttpStatus.NOT_FOUND, MSG_NOT_FOUND_USER),

    // 포인트 관련
    ERR_INVALID_CHARGE_AMOUNT(HttpStatus.BAD_REQUEST, MSG_INVALID_CHARGE_AMOUNT),
    ERR_NOT_ENOUGH_POINT(HttpStatus.BAD_REQUEST, MSG_NOT_ENOUGH_POINT),

    // 주문 관련
    ERR_NOT_FOUND_ORDER(HttpStatus.NOT_FOUND, MSG_NOT_FOUND_ORDER),

    // 결제 관련
    ERR_DUPLICATE_PAYMENT(HttpStatus.CONFLICT, MSG_DUPLICATE_PAYMENT),
    ERR_PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, MSG_PAYMENT_NOT_FOUND),
    ERR_PAYMENT_AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, MSG_PAYMENT_AMOUNT_MISMATCH),
    ERR_PAYMENT_FAILED(HttpStatus.BAD_REQUEST, MSG_PAYMENT_FAILED);


    private final HttpStatus status;
    private final String message;

    ErrorEnum(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}

