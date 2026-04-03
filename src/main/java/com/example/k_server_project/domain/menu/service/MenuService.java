package com.example.k_server_project.domain.menu.service;

import com.example.k_server_project.common.error.ErrorEnum;
import com.example.k_server_project.common.exception.ServiceErrorException;
import com.example.k_server_project.domain.menu.dto.request.MenuCreateRequest;
import com.example.k_server_project.domain.menu.dto.response.MenuCreateResponse;
import com.example.k_server_project.domain.menu.dto.response.MenuListResponse;
import com.example.k_server_project.domain.menu.dto.response.MenuRankingResponse;
import com.example.k_server_project.domain.menu.entity.Menu;
import com.example.k_server_project.domain.menu.repository.MenuRepository;
import com.example.k_server_project.domain.order.service.RankingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final RankingService rankingService;

    // 메뉴 등록
    @Transactional
    @CacheEvict(value = "menuList", allEntries = true)  // 등록시 캐시 전체 삭제
    public MenuCreateResponse createMenu(MenuCreateRequest request) {
        Menu menu = Menu.createMenu(request.getName(), request.getPrice());
        menuRepository.save(menu);
        return new MenuCreateResponse(menu.getId());
    }

    // 메뉴 목록 조회
    @Transactional(readOnly = true)
    @Cacheable(value = "menuList", key = "#keyword != null ? #keyword : 'all'")
    public List<MenuListResponse> getMenuList(String keyword) {
        return menuRepository.findAllMenus(keyword)
                .stream()
                .map(MenuListResponse::new)
                .toList();
    }

    // 주문수 증가
    @Transactional
    public void incrementOrderCount(Long menuId) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new ServiceErrorException(ErrorEnum.ERR_NOT_FOUND_MENU));

        menu.incrementOrderCount();

        log.info("[Menu] 주문수 증가 - menuId={}, orderCount={}", menuId, menu.getOrderCount());
    }

    // 인기 메뉴 TOP 3 조회
    @Transactional(readOnly = true)
    public List<MenuRankingResponse> getPopularMenus() {

        // Redis에서 TOP 3 조회
        List<Long> topMenuIds = rankingService.getTopMenuIds();

        if (topMenuIds.isEmpty()) {
            return new ArrayList<>();
        }

        // menuId로 메뉴 정보 조회
        return topMenuIds.stream()
                .map(menuId -> menuRepository.findById(menuId)
                        .orElseThrow(() -> new ServiceErrorException(ErrorEnum.ERR_NOT_FOUND_MENU)))
                .map(MenuRankingResponse::new)
                .toList();
    }
}
