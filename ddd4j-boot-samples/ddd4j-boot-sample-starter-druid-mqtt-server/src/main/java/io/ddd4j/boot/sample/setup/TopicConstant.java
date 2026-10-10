package io.ddd4j.boot.sample.setup;

/**
 * MQTT / 消息中间件主题（Topic）常量定义类。
 *
 * @author ddd4j
 * @since 4.0.x
 */
public class TopicConstant {

    /**
     * 无参构造，常量类不承载实例状态。
     */
    public TopicConstant() {
    }

    /**
     * Demo 示例主题。
     */
    public static final String DEMO_TOPIC = "demo-topic";

    /**
     * Demo 示例主题（事务消息变体）。
     */
    public static final String DEMO_TOPIC_TS = "demo-topic-ts";

}
