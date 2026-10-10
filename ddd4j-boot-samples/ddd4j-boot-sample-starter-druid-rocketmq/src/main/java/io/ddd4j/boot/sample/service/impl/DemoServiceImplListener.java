package io.ddd4j.boot.sample.service.impl;

import io.ddd4j.boot.sample.entity.DemoEntity;
import io.ddd4j.boot.sample.entity.TxLogEntity;
import io.ddd4j.boot.sample.mapper.TxLogMapper;
import io.ddd4j.boot.sample.service.IDemoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * RocketMQ 本地事务监听器：在消息发送后执行本地事务，并在回查阶段核对事务日志决定提交或回滚。
 *
 * @author ddd4j
 * @since 4.0.x
 */
@Component
@RocketMQTransactionListener
@Slf4j
public class DemoServiceImplListener implements RocketMQLocalTransactionListener {

    /**
     * 无参构造，保持 Spring Bean 默认实例化语义。
     */
    public DemoServiceImplListener() {
    }

    /**
     * 事务日志 Mapper，用于回查阶段核对本地事务是否已落库。
     */
    // 注入事务日志
    @Autowired
    private TxLogMapper txLogMapper;

    /**
     * Demo 示例服务，承载事务消息对应的本地事务逻辑。
     */
    @Autowired
    private IDemoService demoService;

    /**
     * 执行本地事务
     *
     * @param message 消息对象
     * @param arg     本地事务参数
     * @return 本地事务状态
     */
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object arg) {
        log.info("执行本地事务");
        String txId = MapUtils.getString(message.getHeaders(), "txId");
        try {
            // 本地事物
            demoService.doSave(txId, (DemoEntity) arg);
            return RocketMQLocalTransactionState.COMMIT;
        } catch (Exception e) {
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    /**
     * 检查本地事务
     *
     * @param msg 消息对象
     * @return 本地事务状态
     */
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        log.info("检查本地事务");
        // 查询日志记录
        TxLogEntity txLog = txLogMapper.selectById((String) msg.getHeaders().get("txId"));
        if (txLog == null) {
            return RocketMQLocalTransactionState.COMMIT;
        } else {
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

}
