package com.example.k_server_project.domain.menu.repository;

import com.example.k_server_project.domain.menu.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long>, MenuCustomRepository {
}