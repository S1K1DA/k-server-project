package com.example.k_server_project.domain.menu.repository;

import com.example.k_server_project.domain.menu.entity.Menu;

import java.util.List;

public interface MenuCustomRepository {

    List<Menu> findAllMenus(String keyword);

}
