package com.example.k_server_project.domain.menu.service;

import com.example.k_server_project.domain.menu.dto.request.MenuCreateRequest;
import com.example.k_server_project.domain.menu.dto.response.MenuCreateResponse;
import com.example.k_server_project.domain.menu.entity.Menu;
import com.example.k_server_project.domain.menu.repository.MenuRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;

    // 메뉴 등록
    @Transactional
    public MenuCreateResponse createMenu(MenuCreateRequest request) {
        Menu menu = Menu.createMenu(request.getName(), request.getPrice());
        menuRepository.save(menu);
        return new MenuCreateResponse(menu.getId());
    }
}
