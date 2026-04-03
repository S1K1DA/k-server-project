package com.example.k_server_project.domain.menu.controller;

import com.example.k_server_project.common.response.BaseResponse;
import com.example.k_server_project.domain.menu.dto.request.MenuCreateRequest;
import com.example.k_server_project.domain.menu.dto.response.MenuCreateResponse;
import com.example.k_server_project.domain.menu.dto.response.MenuListResponse;
import com.example.k_server_project.domain.menu.dto.response.MenuRankingResponse;
import com.example.k_server_project.domain.menu.service.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    // 메뉴 목록 조회
    @GetMapping
    public ResponseEntity<BaseResponse<List<MenuListResponse>>> getMenuList(
            @RequestParam(required = false) String keyword
    ) {
        List<MenuListResponse> response = menuService.getMenuList(keyword);

        return ResponseEntity.ok(
                BaseResponse.success("200", "메뉴 목록 조회 성공", response)
        );
    }

    // 인기 메뉴 TOP 3 조회
    @GetMapping("/popular")
    public ResponseEntity<BaseResponse<List<MenuRankingResponse>>> getPopularMenus() {
        List<MenuRankingResponse> response = menuService.getPopularMenus();

        return ResponseEntity.ok(
                BaseResponse.success("200", "인기 메뉴 조회 성공", response)
        );
    }
}
