package com.example.k_server_project.domain.menu.repository;

import com.example.k_server_project.domain.menu.entity.Menu;
import com.example.k_server_project.domain.menu.entity.QMenu;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MenuCustomRepositoryImpl implements MenuCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Menu> findAllMenus(String keyword) {
        QMenu menu = QMenu.menu;

        return queryFactory
                .selectFrom(menu)
                .where(keywordCond(keyword))
                .orderBy(menu.id.asc())
                .fetch();
    }

    // 키워드 동적 조건
    private BooleanExpression keywordCond(String keyword) {
        return keyword != null ? QMenu.menu.name.contains(keyword) : null;

    }
}
