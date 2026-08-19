package io.ddd4j.boot.tx.seata;

import io.ddd4j.tx.TransactionContext;
import io.ddd4j.tx.TransactionException;
import io.ddd4j.tx.TransactionManager;
import io.ddd4j.tx.TransactionStatus;
import io.seata.core.context.RootContext;
import io.seata.core.exception.TransactionException;
import io.seata.tm.api.GlobalTransaction;
import io.seata.tm.api.GlobalTransactionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Seata 分布式事务管理器实现。
 *
 * <p>通过 Seata {@code GlobalTransaction} API 驱动 TCC/AT 二阶段提交，
 * 业务方只需依赖 {@link TransactionManager} 接口，本管理器自动协调事务生命周期。
 *
 * @author hiwepy
 * @since 3.4.x
 */
public class SeataTransactionManager implements TransactionManager {

    private static final Logger log = LoggerFactory.getLogger(SeataTransactionManager.class);

    @Override
    public TransactionContext begin(String name, int timeoutMs) {
        try {
            GlobalTransaction tx = GlobalTransactionContext.getCurrentOrCreate();
            tx.begin(timeoutMs, name);
            String xid = RootContext.getXID();
            log.info("Seata transaction begin: name={}, xid={}, timeout={}ms", name, xid, timeoutMs);
            return TransactionContext.builder()
                    .xid(xid)
                    .name(name)
                    .timeoutMs(timeoutMs)
                    .status(TransactionStatus.ACTIVE)
                    .build();
        } catch (TransactionException e) {
            throw new io.ddd4j.tx.TransactionException("Failed to begin Seata transaction: " + name, e);
        }
    }

    @Override
    public void commit(TransactionContext context) {
        try {
            GlobalTransaction tx = GlobalTransactionContext.getCurrentOrCreate();
            tx.commit();
            log.info("Seata transaction commit: xid={}", context.getXid());
        } catch (TransactionException e) {
            throw new io.ddd4j.tx.TransactionException("Failed to commit Seata transaction: " + context.getXid(), e);
        }
    }

    @Override
    public void rollback(TransactionContext context) {
        try {
            GlobalTransaction tx = GlobalTransactionContext.getCurrentOrCreate();
            tx.rollback();
            log.info("Seata transaction rollback: xid={}", context.getXid());
        } catch (TransactionException e) {
            throw new io.ddd4j.tx.TransactionException("Failed to rollback Seata transaction: " + context.getXid(), e);
        }
    }

    @Override
    public TransactionStatus getStatus() {
        String xid = RootContext.getXID();
        if (xid == null) {
            return TransactionStatus.UNKNOWN;
        }
        return TransactionStatus.ACTIVE;
    }

    @Override
    public String getXid() {
        return RootContext.getXID();
    }

    @Override
    public TransactionContext suspend() {
        String xid = RootContext.unbind();
        if (xid != null) {
            log.debug("Seata transaction suspended: xid={}", xid);
            return TransactionContext.ofXid(xid);
        }
        return null;
    }

    @Override
    public void resume(TransactionContext context) {
        if (context != null && context.getXid() != null) {
            RootContext.bind(context.getXid());
            log.debug("Seata transaction resumed: xid={}", context.getXid());
        }
    }
}
