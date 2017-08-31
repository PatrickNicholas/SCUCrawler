package net.hashcoding.code.scucrawler.db.mapper;

import net.hashcoding.code.scucrawler.db.entity.Article;

import java.util.Date;

public interface ArticleMapper {
    Article selectWithUrl(String url);

    Article selectWithUrlAndTimeout(String url, Date date);

    void update(Article article);

    void insert(Article article);
}
