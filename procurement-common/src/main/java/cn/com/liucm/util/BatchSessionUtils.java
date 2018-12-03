package cn.com.liucm.util;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionHolder;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import static org.springframework.util.Assert.notNull;

/**
 * mostly borrowed from {@link org.mybatis.spring.SqlSessionUtils}
 *
 * @author yangk
 * @version 1.0, 2014/04/23
 *
 * @see org.mybatis.spring.SqlSessionUtils
 */
public class BatchSessionUtils {

    private static final Log logger = LogFactory.getLog(BatchSessionUtils.class);

    /**
     * Gets an SqlSession from Spring Transaction Manager or creates a new one if needed.
     * Tries to get a SqlSession out of current transaction. If there is not any, it creates a new one.
     * Then, it synchronizes the SqlSession with the transaction if Spring TX is active and
     * <code>SpringManagedTransactionFactory</code> is configured as a transaction manager.
     *
     * @param sessionFactory      a MyBatis {@code SqlSessionFactory} to create new sessions
     * @param exceptionTranslator Optional. Translates SqlSession.commit() exceptions to Spring exceptions.
     * @throws org.springframework.dao.TransientDataAccessResourceException if a transaction is active and the
     *                                                                      {@code SqlSessionFactory} is not using a {@code SpringManagedTransactionFactory}
     * @see org.mybatis.spring.transaction.SpringManagedTransactionFactory
     */
    public static SqlSession getSqlSession(SqlSessionFactory sessionFactory, PersistenceExceptionTranslator exceptionTranslator) {

        ExecutorType executorType = ExecutorType.BATCH;

        notNull(sessionFactory, "No SqlSessionFactory specified");
        notNull(executorType, "No ExecutorType specified");

        SqlSessionHolder holder = (SqlSessionHolder) TransactionSynchronizationManager.getResource(new SessionFactoryAndExecutorType(sessionFactory, executorType));

        if (holder != null && holder.isSynchronizedWithTransaction()) {
            if (holder.getExecutorType() != executorType) {
                throw new TransientDataAccessResourceException("Cannot change the ExecutorType when there is an existing transaction");
            }

            holder.requested();

            if (logger.isDebugEnabled()) {
                logger.debug("Fetched SqlSession [" + holder.getSqlSession() + "] from current transaction");
            }

            return holder.getSqlSession();
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Creating a new SqlSession");
        }

        SqlSession session = sessionFactory.openSession(executorType);


        // Register session holder if synchronization is active (i.e. a Spring TX is active)
        //
        // Note: The DataSource used by the Environment should be synchronized with the
        // transaction either through DataSourceTxMgr or another tx synchronization.
        // Further assume that if an exception is thrown, whatever started the transaction will
        // handle closing / rolling back the Connection associated with the SqlSession.
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            Environment environment = sessionFactory.getConfiguration().getEnvironment();

            if (environment.getTransactionFactory() instanceof SpringManagedTransactionFactory) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Registering transaction synchronization for SqlSession [" + session + "]");
                }

                holder = new SqlSessionHolder(session, executorType, exceptionTranslator);
                TransactionSynchronizationManager.bindResource(new SessionFactoryAndExecutorType(sessionFactory, executorType), holder);
                TransactionSynchronizationManager.registerSynchronization(new SqlSessionSynchronization(holder, sessionFactory, executorType));
                holder.setSynchronizedWithTransaction(true);
                holder.requested();
            } else {
                if (TransactionSynchronizationManager.getResource(environment.getDataSource()) == null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("SqlSession [" + session + "] was not registered for synchronization because DataSource is not transactional");
                    }
                } else {
                    throw new TransientDataAccessResourceException(
                            "SqlSessionFactory must be using a SpringManagedTransactionFactory in order to use Spring transaction synchronization");
                }
            }
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("SqlSession [" + session + "] was not registered for synchronization because synchronization is not active");
            }
        }


        return session;
    }

    /**
     * Checks if {@code SqlSession} passed as an argument is managed by Spring {@code TransactionSynchronizationManager}
     * If it is not, it closes it, otherwise it just updates the reference counter and
     * lets Spring call the close callback when the managed transaction ends
     *
     * @param session
     * @param sessionFactory
     */
    public static void closeSqlSession(SqlSession session, SqlSessionFactory sessionFactory) {

        notNull(session, "No SqlSession specified");
        notNull(sessionFactory, "No SqlSessionFactory specified");

        SqlSessionHolder holder = (SqlSessionHolder) TransactionSynchronizationManager.getResource(new SessionFactoryAndExecutorType(sessionFactory, ExecutorType.BATCH));
        if ((holder != null) && (holder.getSqlSession() == session)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Releasing transactional SqlSession [" + session + "]");
            }
            holder.released();
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Closing non transactional SqlSession [" + session + "]");
            }
            session.close();
        }
    }


    /**
     * Callback for cleaning up resources. It cleans TransactionSynchronizationManager and
     * also commits and closes the {@code SqlSession}.
     * It assumes that {@code Connection} life cycle will be managed by
     * {@code DataSourceTransactionManager} or {@code JtaTransactionManager}
     */
    private static final class SqlSessionSynchronization extends TransactionSynchronizationAdapter {

        private final SqlSessionHolder holder;

        private final SqlSessionFactory sessionFactory;

        private ExecutorType executorType;

        private boolean holderActive = true;

        public SqlSessionSynchronization(SqlSessionHolder holder, SqlSessionFactory sessionFactory, ExecutorType executorType) {
            notNull(holder, "Parameter 'holder' must be not null");
            notNull(sessionFactory, "Parameter 'sessionFactory' must be not null");

            this.holder = holder;
            this.sessionFactory = sessionFactory;
            this.executorType = executorType;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getOrder() {
            // order right before any Connection synchronization
            return DataSourceUtils.CONNECTION_SYNCHRONIZATION_ORDER - 1;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void suspend() {
            if (this.holderActive) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Transaction synchronization suspending SqlSession [" + this.holder.getSqlSession() + "]");
                }
                TransactionSynchronizationManager.unbindResource(new SessionFactoryAndExecutorType(this.sessionFactory, executorType));
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void resume() {
            if (this.holderActive) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Transaction synchronization resuming SqlSession [" + this.holder.getSqlSession() + "]");
                }
                TransactionSynchronizationManager.bindResource(new SessionFactoryAndExecutorType(this.sessionFactory, executorType), this.holder);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void beforeCommit(boolean readOnly) {
            // Connection commit or rollback will be handled by ConnectionSynchronization or
            // DataSourceTransactionManager.
            // But, do cleanup the SqlSession / Executor, including flushing BATCH statements so
            // they are actually executed.
            // SpringManagedTransaction will no-op the commit over the jdbc connection
            // TODO This updates 2nd level caches but the tx may be rolledback later on!
            if (TransactionSynchronizationManager.isActualTransactionActive()) {
                try {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Transaction synchronization committing SqlSession [" + this.holder.getSqlSession() + "]");
                    }
                    this.holder.getSqlSession().commit();
                } catch (PersistenceException p) {
                    if (this.holder.getPersistenceExceptionTranslator() != null) {
                        DataAccessException translated = this.holder
                                .getPersistenceExceptionTranslator()
                                .translateExceptionIfPossible(p);
                        if (translated != null) {
                            throw translated;
                        }
                    }
                    throw p;
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void beforeCompletion() {
            // Issue #18 Close SqlSession and deregister it now
            // because afterCompletion may be called from a different thread
            if (!this.holder.isOpen()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Transaction synchronization deregistering SqlSession [" + this.holder.getSqlSession() + "]");
                }
                TransactionSynchronizationManager.unbindResource(new SessionFactoryAndExecutorType(this.sessionFactory, executorType));
                this.holderActive = false;
                if (logger.isDebugEnabled()) {
                    logger.debug("Transaction synchronization closing SqlSession [" + this.holder.getSqlSession() + "]");
                }
                this.holder.getSqlSession().close();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void afterCompletion(int status) {
            if (this.holderActive) {
                // afterCompletion may have been called from a different thread
                // so avoid failing if there is nothing in this one
                if (logger.isDebugEnabled()) {
                    logger.debug("Transaction synchronization deregistering SqlSession [" + this.holder.getSqlSession() + "]");
                }
                TransactionSynchronizationManager.unbindResourceIfPossible(new SessionFactoryAndExecutorType(this.sessionFactory, executorType));
                this.holderActive = false;
                if (logger.isDebugEnabled()) {
                    logger.debug("Transaction synchronization closing SqlSession [" + this.holder.getSqlSession() + "]");
                }
                this.holder.getSqlSession().close();
            }
            this.holder.reset();
        }
    }


    private static class SessionFactoryAndExecutorType {
        private SqlSessionFactory sqlSessionFactory;
        private ExecutorType executorType;

        private SessionFactoryAndExecutorType(SqlSessionFactory sqlSessionFactory, ExecutorType executorType) {
            this.sqlSessionFactory = sqlSessionFactory;
            this.executorType = executorType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SessionFactoryAndExecutorType)) return false;

            SessionFactoryAndExecutorType that = (SessionFactoryAndExecutorType) o;

            if (executorType != that.executorType) return false;
            if (!sqlSessionFactory.equals(that.sqlSessionFactory)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = sqlSessionFactory.hashCode();
            result = 31 * result + executorType.hashCode();
            return result;
        }
    }
}

