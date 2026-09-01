package io.hiwepy.boot.sample.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.hiwepy.boot.sample.entity.DemoEntity;
import io.hiwepy.boot.sample.entity.TxLogEntity;
import io.hiwepy.boot.sample.mapper.DemoMapper;
import io.hiwepy.boot.sample.mapper.TxLogMapper;
import io.hiwepy.boot.sample.service.IDemoService;
import io.hiwepy.boot.sample.setup.TopicConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

/**
 * <p>
 * Demo示例表 服务实现类
 * </p>
 *
 * @author wandl
 * @since 2023-08-06
 */
@RocketMQMessageListener(
        consumerGroup = "demo",// 消费者分组
        topic = TopicConstant.DEMO_TOPIC,// 要消费的主题
        selectorExpression = "tag1",// 要消费的标签
        consumeMode = ConsumeMode.CONCURRENTLY, // 消费模式:无序和有序
        messageModel = MessageModel.CLUSTERING // 消息模式:广播和集群,默认是集群
)
@Service
@Slf4j
public class DemoServiceImpl extends ServiceImpl<DemoMapper, DemoEntity> implements RocketMQListener<DemoEntity>, IDemoService {

    @Autowired
    private TxLogMapper txLogMapper;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public void onMessage(DemoEntity message) {

    }

    @Override
    public boolean save(DemoEntity demo) {
        // 创建事物消息
        String txId = UUID.randomUUID().toString();
        Message<DemoEntity> message = MessageBuilder.withPayload(demo).setHeader("txId", txId).build();
        // 发送半事务消息
        //destination formats: `topicName:tags` message – message Message arg – ext arg
        TransactionSendResult res = rocketMQTemplate.sendMessageInTransaction(TopicConstant.DEMO_TOPIC_TS + ":tag1", message, demo);
        if (res.getLocalTransactionState().equals(LocalTransactionState.COMMIT_MESSAGE) && res.getSendStatus().equals(SendStatus.SEND_OK)) {
            log.info("【生产者】事物消息发送成功；成功结果：{}", res);
            return true;
        } else {
            log.info("【生产者】事务发送失败：失败原因：{}", res);
            return false;
        }
    }

    // 本地事物
    @Transactional
    public void doSave(String txId, DemoEntity demo) {
        // 本地事物代码
        getBaseMapper().insert(demo);
        //记录日志到数据库,回查使用
        TxLogEntity txLog = new TxLogEntity();
        txLog.setTxLogId(txId);
        txLog.setContent("事物测试");
        txLog.setDate(new Date());
        txLogMapper.insert(txLog);
    }

}