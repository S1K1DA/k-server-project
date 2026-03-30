package com.example.k_server_project.domain.menu.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MenuCreateRequest {

    @NotBlank(message = "메뉴 이름은 필수입니다.")
    private String name;

    @NotNull(message = "메뉴 가격은 필수입니다.")
    private Long price;
}
