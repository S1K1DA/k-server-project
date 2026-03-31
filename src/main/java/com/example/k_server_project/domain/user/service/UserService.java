package com.example.k_server_project.domain.user.service;

import com.example.k_server_project.common.error.ErrorEnum;
import com.example.k_server_project.common.exception.ServiceErrorException;
import com.example.k_server_project.domain.user.entity.User;
import com.example.k_server_project.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // 포인트 충전 (비관적 락!)
    @Transactional
    public User chargePoint(Long userId, Long amount) {  // void → User 반환!
        User user = userRepository.findByIdWithLock(userId)
                .orElseThrow(() -> new ServiceErrorException(ErrorEnum.ERR_NOT_FOUND_USER));

        user.chargePoint(amount);

        log.info("[User] 포인트 충전 완료 - userId={}, amount={}, totalPoint={}",
                userId, amount, user.getPoint());

        return user;
    }
}