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
    ERR_NOT_FOUND_ORDER(HttpStatus.NOT_FOUND, MSG_NOT_FOUND_ORDER);


    private final HttpStatus status;
    private final String message;

    ErrorEnum(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}

