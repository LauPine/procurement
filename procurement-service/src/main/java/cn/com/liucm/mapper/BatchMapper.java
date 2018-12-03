package cn.com.liucm.mapper;

import cn.com.liucm.util.BatchSessionUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * copied from yangk(批量更新或插入或删除)
 *
 * @author liucm
 * @created 2017-06-20
 */
@Component
public class BatchMapper {

    private SqlSessionFactory sqlSessionFactory;

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(BatchMapper.class);

    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    /**
     * Oracle 10G的JDBC Driver限制最大Batch size是16383条
     */
    public static final int MAX_BATCH = 16000;

    public <T> void batchInsert(Class clazz, String methodName, List<T> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        SqlSession sqlSession = BatchSessionUtils.getSqlSession(sqlSessionFactory, null);

        String mappedStatementId = clazz.getName() + "." + methodName;

        try {
            for (int i = 0; i < list.size(); i++) {
                T t = list.get(i);
                sqlSession.insert(mappedStatementId, t);
                if ((i + 1) % MAX_BATCH == 0) {
                    long startTine = System.currentTimeMillis();
                    sqlSession.flushStatements();
                    LOGGER.debug("flush耗时：{}",System.currentTimeMillis()-startTine);
                }
            }

            sqlSession.flushStatements();
        } finally {
            BatchSessionUtils.closeSqlSession(sqlSession, sqlSessionFactory) ;
        }
    }

    public <T> void batchInsert(Class clazz, List<T> list){
        batchInsert(clazz, "insert", list);
    }

    public <T> void batchDelete(Class clazz, String methodName, List<T> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        SqlSession sqlSession = BatchSessionUtils.getSqlSession(sqlSessionFactory, null);

        String mappedStatementId = clazz.getName() + "." + methodName;

        try {
            for (int i = 0; i < list.size(); i++) {
                T t = list.get(i);
                sqlSession.delete(mappedStatementId, t);
                if ((i + 1) % MAX_BATCH == 0) {
                    sqlSession.flushStatements();
                }
            }

            sqlSession.flushStatements();
        } finally {
            BatchSessionUtils.closeSqlSession(sqlSession, sqlSessionFactory) ;
        }
    }

    public <T> void batchDelete(Class clazz, List<T> list){
        batchInsert(clazz, "delete", list);
    }
}
