package io.ddd4j.boot.mq.impl;

import io.ddd4j.boot.core.contract.MQEvent;
import io.ddd4j.boot.core.contract.exception.ServiceException;
import io.ddd4j.boot.core.utils.JsonKit;
import io.ddd4j.boot.mq.core.MQClient;
import io.ddd4j.boot.mq.core.MQListener;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Rocket客户端实现
 */
@Slf4j(topic = "### BASE-MQ : rocketClient ###")
public final class RocketClient implements MQClient {
    private static Map<String, Supplier> LISTENERS = new ConcurrentHashMap<>();

    @Override
    public String impl() {
        return "rocket";
    }

    @Override
    public Consumer<MQEvent> initProducer() {
        try {
            config().setConcat(".");
            // 创建 DefaultMQProducer 实例
            DefaultMQProducer rocketProducer;
            if (!config().getUsername().isEmpty() && !config().getPassword().isEmpty()) {
                // 需要鉴权
                rocketProducer = new DefaultMQProducer(config().getNamespace(), config().getProducerGroup(),
                        new AclClientRPCHook(new SessionCredentials(config().getUsername(),
                                config().getPassword())));
            } else {
                rocketProducer = new DefaultMQProducer(config().getNamespace(), config().getProducerGroup());
            }
            rocketProducer.setNamesrvAddr(config().getServer());
            // 启动rocketProducer实例
            rocketProducer.start();
            DefaultMQProducer finalRocketProducer = rocketProducer;
            return mqEvent -> {
                // 定义具体的MQ事件发布逻辑
                String message = serialization().serialize(mqEvent);
                String tags = mqEvent.getTag() == null ? "" : mqEvent.getTag();
                try {
                    finalRocketProducer.send(new Message(mqEvent.getTopic(), tags, message.getBytes(RemotingHelper.DEFAULT_CHARSET)));
                    log.info("Publish MQ [{}]: {}", mqEvent.getTopic() + "." + tags, message);
                } catch (Exception e) {
                    log.error("Publish MQ [{}]: {} failed!", mqEvent.getTopic() + "." + tags, message, e);
                }
            };
        } catch (MQClientException e) {
            log.error("创建Rocket生产者失败", e);
        }
        return null;
    }

    @Override
    public boolean initConsumer(MQListener mqListener) throws Exception {
        DefaultMQPushConsumer defaultMQPushConsumer;
        if (!config().getUsername().isEmpty() && !config().getPassword().isEmpty()) {
            // 需要鉴权的情况
            defaultMQPushConsumer = new DefaultMQPushConsumer(config().getNamespace(), mqListener.getGroup(),
                    new AclClientRPCHook(new SessionCredentials(config().getUsername(), config().getPassword())));
        } else {
            defaultMQPushConsumer = new DefaultMQPushConsumer(config().getNamespace(), mqListener.getGroup());
        }
        defaultMQPushConsumer.setNamesrvAddr(config().getServer());
        defaultMQPushConsumer.subscribe(mqListener.getTopic(), mqListener.getTags());
        // 创建MessageListenerConcurrently实例
        defaultMQPushConsumer.registerMessageListener((MessageListenerConcurrently) (messageExts, context) -> {
            // 获取方法参数
            for (MessageExt messageExt : messageExts) {
                MQEvent mqEvent = null;
                try {
                    // 反序列化为MQEvent对象
                    mqEvent = mqListener.getDeserialize().apply(new String(messageExt.getBody(), StandardCharsets.UTF_8));
                    if (mqEvent != null) {
                        consume(mqListener, mqEvent);
                    } else {
                        log.warn("Consume MQ [{}] failed: the mqEvent is null", mqListener.namespaceTopicTags());
                    }
                } catch (Throwable e) {
                    if (e instanceof ServiceException) {
                        log.error("Consume MQ failed: {} => {}", e.getMessage(), JsonKit.toJson(mqEvent));
                    } else {
                        log.error("Consume MQ failed: {}", mqEvent, e);
                    }
                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        LISTENERS.put(mqListener.getTopic(), () -> defaultMQPushConsumer);
        return true;
    }

    @Override
    @SneakyThrows
    public void start() {
        if (!config().isEnable() || !Objects.equals(config().getImpl(), impl())) return;
        // 在此统一启动所有消费者
        for (Supplier<DefaultMQPushConsumer> listener : LISTENERS.values()) {
            listener.get().start();
        }
    }
}