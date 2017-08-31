package net.hashcoding.code.scucrawler.db;

import net.hashcoding.code.scucrawler.db.entity.Article;
import net.hashcoding.code.scucrawler.db.entity.ArticleType;
import net.hashcoding.code.scucrawler.db.mapper.ArticleMapper;
import net.hashcoding.code.scucrawler.db.mapper.ArticleTypeMapper;
import net.hashcoding.code.scucrawler.utils.MyBatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;

public class ArticleAPI {
    private static final Logger logger = LoggerFactory.getLogger(ArticleAPI.class);

    public static void insertOrUpdate(String url, String typeName,
                                      String thumb, String title, String content) {
        SqlSession session = MyBatisUtils.getSqlSession(false);
        try {
            ArticleMapper mapper = session.getMapper(ArticleMapper.class);
            ArticleTypeMapper typeMapper = session.getMapper(ArticleTypeMapper.class);
            ArticleType type = typeMapper.selectWithName(typeName);
            Article article = mapper.selectWithUrl(url);
            if (article == null) {
                article = new Article();
                article.setUrl(url);
                article.setType(type);
                article.setTitle(title);
                article.setContent(content);
                mapper.insert(article);
            } else {
                article.setType(type);
                article.setTitle(title);
                article.setContent(content);
                mapper.update(article);
            }
            session.commit();
        } catch (Exception e) {
            session.rollback();
            logger.error("Failed to update: ", e);
        } finally {
            session.close();
        }
    }

    public static boolean isURLExists(String url) {
        try (SqlSession session = MyBatisUtils.getSqlSession()) {
            ArticleMapper mapper = session.getMapper(ArticleMapper.class);
            return mapper.selectWithUrl(url) != null;
        } catch (Exception e) {
            logger.error("Failed to query: ", e);
        }
        return false;
    }

    public static boolean isURLExistsAndTimeout(String url) {
        try (SqlSession session = MyBatisUtils.getSqlSession()) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR, 6);
            Date timeout = calendar.getTime();

            ArticleMapper mapper = session.getMapper(ArticleMapper.class);
            Article article = mapper.selectWithUrl(url);
            return article != null && article.getUpdatedAt().compareTo(timeout) <= 0;
        } catch (Exception e) {
            logger.error("Failed to query: ", e);
        }
        return false;
    }
}
