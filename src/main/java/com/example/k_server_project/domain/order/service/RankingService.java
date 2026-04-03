package com.example.k_server_project.domain.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RankingService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String RANKING_KEY_PREFIX = "menu:ranking:";
    private static final int TOP_COUNT = 3;
    private static final int DAYS = 7;

    // 메뉴 주문 시 점수 +1
    public void incrementMenuScore(Long menuId) {
        String key = getTodayKey();
        redisTemplate.opsForZSet().incrementScore(key, String.valueOf(menuId), 1);

        // 8일 후 자동 삭제!
        redisTemplate.expire(key, 8, TimeUnit.DAYS);

        log.info("[Ranking] 메뉴 점수 증가 - menuId={}, key={}", menuId, key);
    }

    // 7일 인기 메뉴 TOP 3 조회
    public List<Long> getTopMenuIds() {
        List<String> keys = new ArrayList<>();
        for (int i = 0; i < DAYS; i++) {
            keys.add(getKeyByDate(LocalDate.now().minusDays(i)));
        }

        // 7일치 ZSET 합산
        String destKey = "menu:ranking:total";
        redisTemplate.opsForZSet().unionAndStore(
                keys.get(0),
                keys.subList(1, keys.size()),
                destKey
        );

        // TOP 3 조회
        Set<String> topMenus = redisTemplate.opsForZSet()
                .reverseRange(destKey, 0, TOP_COUNT - 1);

        if (topMenus == null || topMenus.isEmpty()) {
            return new ArrayList<>();
        }
        return topMenus.stream()
                .map(Long::valueOf)
                .toList();
    }

    // 오늘 날짜 키
    private String getTodayKey() {
        return getKeyByDate(LocalDate.now());
    }

    // 날짜별 키 생성
    private String getKeyByDate(LocalDate date) {
        return RANKING_KEY_PREFIX + date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}