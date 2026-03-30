package com.example.k_server_project.domain.menu.service;

import com.example.k_server_project.domain.menu.dto.request.MenuCreateRequest;
import com.example.k_server_project.domain.menu.dto.response.MenuCreateResponse;
import com.example.k_server_project.domain.menu.dto.response.MenuListResponse;
import com.example.k_server_project.domain.menu.entity.Menu;
import com.example.k_server_project.domain.menu.repository.MenuRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;

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
}
