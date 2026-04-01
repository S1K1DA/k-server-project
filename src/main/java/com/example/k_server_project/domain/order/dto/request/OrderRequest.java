package com.example.k_server_project.domain.order.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    @NotNull(message = "메뉴 ID는 필수입니다.")
    private Long menuId;

}