package com.example.k_server_project.domain.menu.controller;

import com.example.k_server_project.common.response.BaseResponse;
import com.example.k_server_project.domain.menu.dto.request.MenuCreateRequest;
import com.example.k_server_project.domain.menu.dto.response.MenuCreateResponse;
import com.example.k_server_project.domain.menu.service.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/menus")
public class MenuController {

    private final MenuService menuService;

    // 메뉴 등록
    @PostMapping
    public ResponseEntity<BaseResponse<MenuCreateResponse>> createMenu(
            @Valid @RequestBody MenuCreateRequest request
    ) {
        MenuCreateResponse response = menuService.createMenu(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success("201", "메뉴 등록 성공", response));
    }
}
