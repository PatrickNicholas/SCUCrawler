package net.hashcoding.code.scucrawler.db.mapper;

import net.hashcoding.code.scucrawler.db.entity.ArticleType;

import java.util.List;

public interface ArticleTypeMapper {
    ArticleType selectWithName(String name);

    List<ArticleType> list();
}
