package io.ddd4j.boot.cmpt.tdmq.client;

import lombok.extern.slf4j.Slf4j;

/**
 * TDMQ 客户端占位实现，仅记录日志不触达 Broker。
 */
@Slf4j
public class TdmqClientPlaceholder implements TdmqClient {

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void publish(String topic, String tag, byte[] payload) {
        log.warn("TDMQ client placeholder publish skipped: topic={}, tag={}, size={}",
                topic, tag, payload == null ? 0 : payload.length);
        // TODO: 接入腾讯云 TDMQ SDK（Pulsar/RabbitMQ 兼容模式）
    }
}
