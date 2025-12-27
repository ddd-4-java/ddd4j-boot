package io.ddd4j.boot.cmpt.kafka.consumer;


import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties.Listener;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.*;
import org.springframework.kafka.support.converter.MessageConverter;
import org.springframework.kafka.transaction.KafkaAwareTransactionManager;

import java.time.Duration;

/**
 * Configure {@link ConcurrentKafkaListenerContainerFactory} with sensible defaults.
 *
 * @author Gary Russell
 * @author Eddú Meléndez
 * @since 1.5.0
 */
public class MyConcurrentKafkaListenerContainerFactoryConfigurer extends ConcurrentKafkaListenerContainerFactoryConfigurer {

    private KafkaProperties properties;

    /**
     * the {@link MessageConverter} to use.
     */
    private MessageConverter messageConverter;

    private KafkaTemplate<String, String> replyTemplate;

    private KafkaAwareTransactionManager<String, String> transactionManager;

    private ConsumerAwareRebalanceListener rebalanceListener;

    private ErrorHandler errorHandler;

    private BatchErrorHandler batchErrorHandler;

    private AfterRollbackProcessor<String, String> afterRollbackProcessor;

    private RecordInterceptor<String, String> recordInterceptor;

    /**
     * The ack mode to use when auto ack (in the configuration properties) is false.
     * <ul>
     * <li>RECORD: Ack after each record has been passed to the listener.</li>
     * <li>BATCH: Ack after each batch of records received from the consumer has been
     * passed to the listener</li>
     * <li>TIME: Ack after this number of milliseconds; (should be greater than
     * {@code #setPollTimeout(long) pollTimeout}.</li>
     * <li>COUNT: Ack after at least this number of records have been received</li>
     * <li>MANUAL: Listener is responsible for acking - use a
     * {@link AcknowledgingMessageListener}.
     * </ul>
     */
    private ContainerProperties.AckMode ackMode;

    /**
     * Set the {@link KafkaProperties} to use.
     *
     * @param properties the properties
     */
    public void setKafkaProperties(KafkaProperties properties) {
        this.properties = properties;
    }

    /**
     * Set
     *
     * @param messageConverter the message converter
     */
    public void setMessageConverter(MessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }

    /**
     * Set the {@link KafkaTemplate} to use to send replies.
     *
     * @param replyTemplate the reply template
     */
    public void setReplyTemplate(KafkaTemplate<String, String> replyTemplate) {
        this.replyTemplate = replyTemplate;
    }

    /**
     * Set the {@link KafkaAwareTransactionManager} to use.
     *
     * @param transactionManager the transaction manager
     */
    public void setTransactionManager(KafkaAwareTransactionManager<String, String> transactionManager) {
        this.transactionManager = transactionManager;
    }

    /**
     * Set the {@link ConsumerAwareRebalanceListener} to use.
     *
     * @param rebalanceListener the rebalance listener.
     * @since 2.2
     */
    public void setRebalanceListener(ConsumerAwareRebalanceListener rebalanceListener) {
        this.rebalanceListener = rebalanceListener;
    }

    /**
     * Set the {@link ErrorHandler} to use.
     *
     * @param errorHandler the error handler
     */
    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    /**
     * Set the {@link BatchErrorHandler} to use.
     *
     * @param batchErrorHandler the error handler
     */
    public void setBatchErrorHandler(BatchErrorHandler batchErrorHandler) {
        this.batchErrorHandler = batchErrorHandler;
    }

    /**
     * Set the {@link AfterRollbackProcessor} to use.
     *
     * @param afterRollbackProcessor the after rollback processor
     */
    public void setAfterRollbackProcessor(AfterRollbackProcessor<String, String> afterRollbackProcessor) {
        this.afterRollbackProcessor = afterRollbackProcessor;
    }

    /**
     * Set the {@link RecordInterceptor} to use.
     *
     * @param recordInterceptor the record interceptor.
     */
    public void setRecordInterceptor(RecordInterceptor<String, String> recordInterceptor) {
        this.recordInterceptor = recordInterceptor;
    }

    public void setAckMode(ContainerProperties.AckMode ackMode) {
        this.ackMode = ackMode;
    }

    /**
     * Configure the specified Kafka listener container factory. The factory can be
     * further tuned and default settings can be overridden.
     *
     * @param listenerFactory the {@link ConcurrentKafkaListenerContainerFactory} instance
     *                        to configure
     * @param consumerFactory the {@link ConsumerFactory} to use
     */
    public void configure2(ConcurrentKafkaListenerContainerFactory<String, String> listenerFactory,
                           ConsumerFactory<String, String> consumerFactory) {
        listenerFactory.setConsumerFactory(consumerFactory);
        configureListenerFactory(listenerFactory);
        configureContainer(consumerFactory, listenerFactory.getContainerProperties());
    }

    private void configureListenerFactory(ConcurrentKafkaListenerContainerFactory<String, String> factory) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        Listener properties = this.properties.getListener();
        map.from(properties::getConcurrency).to(factory::setConcurrency);
        map.from(this.messageConverter).to(factory::setMessageConverter);
        map.from(this.replyTemplate).to(factory::setReplyTemplate);
        if (properties.getType().equals(Listener.Type.BATCH)) {
            factory.setBatchListener(true);
            factory.setBatchErrorHandler(this.batchErrorHandler);
        } else {
            factory.setErrorHandler(this.errorHandler);
        }
        map.from(this.afterRollbackProcessor).to(factory::setAfterRollbackProcessor);
        map.from(this.recordInterceptor).to(factory::setRecordInterceptor);
    }

    private void configureContainer(ConsumerFactory<String, String> consumerFactory, ContainerProperties container) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        Listener properties = this.properties.getListener();
        // 如果是自动提交，且ackMode是MANUAL或MANUAL_IMMEDIATE，那么ackMode需要手动设置
        if (consumerFactory.isAutoCommit() && (properties.getAckMode() == ContainerProperties.AckMode.MANUAL
                || properties.getAckMode() == ContainerProperties.AckMode.MANUAL_IMMEDIATE)) {
            map.from(this.ackMode).to(container::setAckMode);
        } else {
            map.from(properties::getAckMode).to(container::setAckMode);
        }
        map.from(properties::getClientId).to(container::setClientId);
        map.from(properties::getAckCount).to(container::setAckCount);
        map.from(properties::getAckTime).as(Duration::toMillis).to(container::setAckTime);
        map.from(properties::getPollTimeout).as(Duration::toMillis).to(container::setPollTimeout);
        map.from(properties::getNoPollThreshold).to(container::setNoPollThreshold);
        map.from(properties::getIdleEventInterval).as(Duration::toMillis).to(container::setIdleEventInterval);
        map.from(properties::getMonitorInterval).as(Duration::getSeconds).as(Number::intValue)
                .to(container::setMonitorInterval);
        map.from(properties::getLogContainerConfig).to(container::setLogContainerConfig);
        map.from(properties::isMissingTopicsFatal).to(container::setMissingTopicsFatal);
        map.from(this.transactionManager).to(container::setTransactionManager);
        map.from(this.rebalanceListener).to(container::setConsumerRebalanceListener);
    }

}
