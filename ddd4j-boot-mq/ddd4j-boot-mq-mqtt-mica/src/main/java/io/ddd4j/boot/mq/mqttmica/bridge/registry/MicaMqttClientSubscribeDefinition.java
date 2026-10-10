package io.ddd4j.boot.mq.mqttmica.bridge.registry;

import org.dromara.mica.mqtt.codec.MqttQoS;
import org.dromara.mica.mqtt.core.annotation.MqttClientSubscribe;
import org.dromara.mica.mqtt.core.deserialize.MqttDeserializer;

import java.lang.reflect.Method;

/**
 * {@link MqttClientSubscribe} 解析后的原生订阅定义。
 *
 * <p>不可变值对象：由扫描器从方法级或类级注解解析生成，供注册器登记、
 * 桥接启动时按定义创建原生 MQTT 订阅。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
public class MicaMqttClientSubscribeDefinition {

    /** 承载监听逻辑的 Bean 实例。 */
    private final Object bean;
    /** 上述 Bean 在 Spring 容器中的名称，用于日志与去重。 */
    private final String beanName;
    /** 方法级监听对应的目标方法；类级监听时为 {@code null}。 */
    private final Method method;
    /** 注解上声明的 topic 模板（可能包含占位符）。 */
    private final String[] topicTemplates;
    /** 展开占位符后的实际 topic 过滤器。 */
    private final String[] topicFilters;
    /** 订阅服务质量等级。 */
    private final MqttQoS qos;
    /** 目标 MQTT 客户端模板 Bean 名称（空则使用默认客户端）。 */
    private final String clientTemplateBean;
    /** 消息反序列化器类型。 */
    private final Class<? extends MqttDeserializer> deserializerType;
    /** 是否为类级监听（整个实现类统一订阅）。 */
    private final boolean classLevelListener;

    /**
     * 私有全参构造器，仅供本类两个静态工厂方法调用，保证实例不可变。
     *
     * @param bean 承载监听逻辑的 Bean 实例
     * @param beanName Bean 名称
     * @param method 方法级监听的目标方法（类级监听传 {@code null}）
     * @param topicTemplates topic 模板
     * @param topicFilters 展开后的实际 topic 过滤器
     * @param qos 服务质量等级
     * @param clientTemplateBean 客户端模板 Bean 名称
     * @param deserializerType 反序列化器类型
     * @param classLevelListener 是否类级监听
     */
    private MicaMqttClientSubscribeDefinition(
            Object bean,
            String beanName,
            Method method,
            String[] topicTemplates,
            String[] topicFilters,
            MqttQoS qos,
            String clientTemplateBean,
            Class<? extends MqttDeserializer> deserializerType,
            boolean classLevelListener) {
        this.bean = bean;
        this.beanName = beanName;
        this.method = method;
        this.topicTemplates = topicTemplates;
        this.topicFilters = topicFilters;
        this.qos = qos;
        this.clientTemplateBean = clientTemplateBean;
        this.deserializerType = deserializerType;
        this.classLevelListener = classLevelListener;
    }

    /**
     * 获取承载监听逻辑的 Bean 实例。
     *
     * @return Bean 实例
     */
    public Object getBean() {
        return bean;
    }

    /**
     * 获取 Bean 在容器中的名称。
     *
     * @return Bean 名称
     */
    public String getBeanName() {
        return beanName;
    }

    /**
     * 获取方法级监听的目标方法。
     *
     * @return 目标方法；类级监听时返回 {@code null}
     */
    public Method getMethod() {
        return method;
    }

    /**
     * 获取注解声明的 topic 模板。
     *
     * @return topic 模板数组
     */
    public String[] getTopicTemplates() {
        return topicTemplates;
    }

    /**
     * 获取展开占位符后的实际 topic 过滤器。
     *
     * @return topic 过滤器数组
     */
    public String[] getTopicFilters() {
        return topicFilters;
    }

    /**
     * 获取订阅服务质量等级。
     *
     * @return QoS 等级
     */
    public MqttQoS getQos() {
        return qos;
    }

    /**
     * 获取目标 MQTT 客户端模板 Bean 名称。
     *
     * @return 客户端模板 Bean 名称，未指定时为注解默认值
     */
    public String getClientTemplateBean() {
        return clientTemplateBean;
    }

    /**
     * 获取反序列化器类型。
     *
     * @return 反序列化器类型
     */
    public Class<? extends MqttDeserializer> getDeserializerType() {
        return deserializerType;
    }

    /**
     * 判断是否为类级监听。
     *
     * @return {@code true} 表示类级监听
     */
    public boolean isClassLevelListener() {
        return classLevelListener;
    }

    /**
     * 从方法级注解构建定义。
     *
     * @param bean 承载监听逻辑的 Bean 实例
     * @param beanName Bean 名称
     * @param method 被标注的方法
     * @param subscribe 方法上的 {@link MqttClientSubscribe} 注解
     * @param topicFilters 展开占位符后的实际 topic 过滤器
     * @return 方法级订阅定义
     */
    public static MicaMqttClientSubscribeDefinition fromMethod(
            Object bean,
            String beanName,
            Method method,
            MqttClientSubscribe subscribe,
            String[] topicFilters) {
        return new MicaMqttClientSubscribeDefinition(
                bean,
                beanName,
                method,
                subscribe.value(),
                topicFilters,
                subscribe.qos(),
                subscribe.clientTemplateBean(),
                subscribe.deserialize(),
                false);
    }

    /**
     * 从类级注解构建定义（{@code IMqttClientMessageListener} 实现类）。
     *
     * @param bean 承载监听逻辑的 Bean 实例
     * @param beanName Bean 名称
     * @param subscribe 类上的 {@link MqttClientSubscribe} 注解
     * @param topicFilters 展开占位符后的实际 topic 过滤器
     * @return 类级订阅定义
     */
    public static MicaMqttClientSubscribeDefinition fromClass(
            Object bean,
            String beanName,
            MqttClientSubscribe subscribe,
            String[] topicFilters) {
        return new MicaMqttClientSubscribeDefinition(
                bean,
                beanName,
                null,
                subscribe.value(),
                topicFilters,
                subscribe.qos(),
                subscribe.clientTemplateBean(),
                subscribe.deserialize(),
                true);
    }
}
