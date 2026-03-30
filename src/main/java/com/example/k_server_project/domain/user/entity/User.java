package com.example.k_server_project.domain.user.entity;

import com.example.k_server_project.common.entity.BaseEntity;
import com.example.k_server_project.common.error.ErrorEnum;
import com.example.k_server_project.common.exception.ServiceErrorException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.example.k_server_project.common.entity.BaseEntity;
import com.example.k_server_project.common.error.ErrorEnum;
import com.example.k_server_project.common.exception.ServiceErrorException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 유저 이름
    @Column(name = "name", nullable = false)
    private String name;

    // 포인트 잔액
    @Column(name = "point", nullable = false)
    private Long point = 0L;

    // 유저 생성
    public static User createUser(String name) {
        User user = new User();
        user.name = name;
        user.point = 0L;
        return user;
    }

    // 포인트 충전
    public void chargePoint(Long amount) {
        if (amount <= 0) {
            throw new ServiceErrorException(ErrorEnum.ERR_INVALID_CHARGE_AMOUNT);
        }
        this.point += amount;
    }

    // 포인트 차감
    public void deductPoint(Long amount) {
        if (this.point < amount) {
            throw new ServiceErrorException(ErrorEnum.ERR_NOT_ENOUGH_POINT);
        }
        this.point -= amount;
    }
}

