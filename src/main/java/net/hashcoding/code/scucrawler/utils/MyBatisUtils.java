package net.hashcoding.code.scucrawler.utils;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

public class MyBatisUtils {

    private static Logger logger = LoggerFactory.getLogger(MyBatisUtils.class);

    private volatile static SqlSessionFactory factory;

    /**
     * 获取SqlSessionFactory
     *
     * @return SqlSessionFactory
     */
    public static SqlSessionFactory getSqlSessionFactory() {
        try {
            if (factory == null) synchronized (MyBatisUtils.class) {
                if (factory == null) {
                    InputStream is = MyBatisUtils.class.getClassLoader()
                            .getResourceAsStream("db/config.xml");
                    factory = new SqlSessionFactoryBuilder().build(is);
                }
            }
            return factory;
        } catch (Exception e) {
            logger.error("failed to create SqlSessionFactory", e);
        }
        return null;
    }

    /**
     * 获取SqlSession
     *
     * @return SqlSession
     */
    public static SqlSession getSqlSession() {
        return getSqlSessionFactory().openSession();
    }

    /**
     * 获取SqlSession
     *
     * @param isAutoCommit true 表示创建的SqlSession对象在执行完SQL之后会自动提交事务
     *                     false 表示创建的SqlSession对象在执行完SQL之后不会自动提交事务，这时就需要我们手动调用sqlSession.commit()提交事务
     * @return SqlSession
     */
    public static SqlSession getSqlSession(boolean isAutoCommit) {
        return getSqlSessionFactory().openSession(isAutoCommit);
    }
}