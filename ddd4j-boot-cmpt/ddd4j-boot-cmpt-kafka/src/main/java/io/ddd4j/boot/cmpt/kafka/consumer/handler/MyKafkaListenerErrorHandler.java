package io.ddd4j.boot.cmpt.kafka.consumer.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * 异常处理
 */
@Component
@Slf4j
public class MyKafkaListenerErrorHandler implements KafkaListenerErrorHandler {

    @Override
    @NonNull
    public Object handleError(@NonNull Message<?> message, @NonNull ListenerExecutionFailedException exception) {
        return new Object();
    }

    @Override
    @NonNull
    public Object handleError(@NonNull Message<?> message, @NonNull ListenerExecutionFailedException exception,
                              Consumer<?, ?> consumer) {
        log.info("消息详情：" + message);
        log.info("异常信息：：" + exception);
        log.info("消费者详情：：" + consumer.groupMetadata());
        log.info("监听主题：：" + consumer.listTopics());
        // TODO 消费记录
        return KafkaListenerErrorHandler.super.handleError(message, exception, consumer);
    }
}
