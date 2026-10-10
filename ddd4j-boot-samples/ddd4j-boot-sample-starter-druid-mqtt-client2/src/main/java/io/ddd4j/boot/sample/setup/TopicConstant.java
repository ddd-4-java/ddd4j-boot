package io.ddd4j.boot.sample.setup;

/**
 * MQTT 主题名称常量定义类。
 * <p>
 * 统一维护示例中使用的主题字符串，避免发布端与订阅端硬编码导致的主题漂移。
 * </p>
 */
public class TopicConstant {

    /**
     * 常规演示主题名称。
     */
    public static final String DEMO_TOPIC = "demo-topic";

    /**
     * 事务型演示主题名称。
     */
    public static final String DEMO_TOPIC_TS = "demo-topic-ts";

    /**
     * 构造主题常量类（显式无参构造器，与编译器生成的默认构造器等价）。
     */
    public TopicConstant() {
    }

}
