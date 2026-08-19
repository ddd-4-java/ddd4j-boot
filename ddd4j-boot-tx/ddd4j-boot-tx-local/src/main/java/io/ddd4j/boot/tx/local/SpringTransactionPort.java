package io.ddd4j.boot.tx.local;

import io.ddd4j.tx.TransactionException;
import io.ddd4j.tx.TransactionPort;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.Objects;

/**
 * Spring 本地事务端口实现。
 *
 * <p>基于 Spring {@link PlatformTransactionManager} 实现 {@link TransactionPort}，
 * 支持声明式事务管理（@Transactional 语义）。
 *
 * @author hiwepy
 * @since 3.4.x
 */
public class SpringTransactionPort implements TransactionPort {

    private final PlatformTransactionManager transactionManager;
    private final TransactionDefinition transactionDefinition;

    public SpringTransactionPort(PlatformTransactionManager transactionManager) {
        this(transactionManager, new DefaultTransactionDefinition());
    }

    public SpringTransactionPort(PlatformTransactionManager transactionManager,
                                  TransactionDefinition transactionDefinition) {
        this.transactionManager = Objects.requireNonNull(transactionManager, "transactionManager must not be null");
        this.transactionDefinition = Objects.requireNonNull(transactionDefinition, "transactionDefinition must not be null");
    }

    @Override
    public void execute(Runnable operation) {
        TransactionStatus status = transactionManager.getTransaction(transactionDefinition);
        try {
            operation.run();
            transactionManager.commit(status);
        } catch (RuntimeException | Error e) {
            transactionManager.rollback(status);
            throw new TransactionException("Local transaction failed", e);
        }
    }
}
