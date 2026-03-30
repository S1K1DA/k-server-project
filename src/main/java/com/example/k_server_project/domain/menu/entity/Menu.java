package com.example.k_server_project.domain.menu.entity;

import com.example.k_server_project.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "menus")
public class Menu extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private Long price;

    // 메뉴 생성
    public static Menu createMenu(String name, Long price) {
        Menu menu = new Menu();
        menu.name = name;
        menu.price = price;
        return menu;
    }
}
