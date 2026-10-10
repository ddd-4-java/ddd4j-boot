package io.ddd4j.boot.sample.setup;

/**
 * Kafka 主题名称常量定义类。
 * <p>
 * 集中维护示例演示所使用的主题名，供生产者与消费者统一引用，避免硬编码漂移。
 * </p>
 */
public class TopicConstant {

    /** 演示用常规消息主题名称。 */
    public static final String DEMO_TOPIC = "demo-topic";

    /** 演示用事务型消息主题名称。 */
    public static final String DEMO_TOPIC_TS = "demo-topic-ts";

    /**
     * 构造主题常量定义类（显式无参构造器，与编译器生成的默认构造器等价）。
     */
    public TopicConstant() {
    }

}
