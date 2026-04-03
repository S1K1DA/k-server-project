package com.example.k_server_project.domain.menu.dto.response;

import com.example.k_server_project.domain.menu.entity.Menu;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MenuRankingResponse {

    private Long menuId;
    private String name;
    private Long price;
    private Long orderCount;

    public MenuRankingResponse(Menu menu) {
        this.menuId = menu.getId();
        this.name = menu.getName();
        this.price = menu.getPrice();
        this.orderCount = menu.getOrderCount();
    }
}