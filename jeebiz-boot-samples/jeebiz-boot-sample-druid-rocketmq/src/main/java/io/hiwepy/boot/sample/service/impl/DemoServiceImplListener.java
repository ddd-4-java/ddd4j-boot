package io.hiwepy.boot.sample.service.impl;

import io.hiwepy.boot.sample.entity.DemoEntity;
import io.hiwepy.boot.sample.entity.TxLogEntity;
import io.hiwepy.boot.sample.mapper.TxLogMapper;
import io.hiwepy.boot.sample.service.IDemoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@RocketMQTransactionListener
@Slf4j
public class DemoServiceImplListener implements RocketMQLocalTransactionListener {

    // 注入事务日志
    @Autowired
    private TxLogMapper txLogMapper;
    @Autowired
    private IDemoService demoService;

    /**
     * 执行本地事务
     * @param message 消息对象
     * @param arg 本地事务参数
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
