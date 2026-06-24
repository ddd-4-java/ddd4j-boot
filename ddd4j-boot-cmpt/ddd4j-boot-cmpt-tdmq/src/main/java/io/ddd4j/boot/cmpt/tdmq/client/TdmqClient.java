package io.ddd4j.boot.cmpt.tdmq.client;

/**
 * 腾讯云 TDMQ 客户端占位接口（待接入官方 tdmq-client / Pulsar 兼容 SDK）。
 */
public interface TdmqClient {

    /**
     * 客户端是否已就绪。
     *
     * @return 就绪时 true
     */
    boolean isReady();

    /**
     * 发布消息到指定 topic。
     *
     * @param topic   主题
     * @param tag     标签
     * @param payload 消息体
     */
    void publish(String topic, String tag, byte[] payload);
}
