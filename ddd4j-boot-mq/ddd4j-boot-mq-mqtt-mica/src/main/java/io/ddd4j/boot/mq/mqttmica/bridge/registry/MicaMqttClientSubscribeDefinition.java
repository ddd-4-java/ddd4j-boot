package io.ddd4j.boot.mq.mqttmica.bridge.registry;

import org.dromara.mica.mqtt.codec.MqttQoS;
import org.dromara.mica.mqtt.core.annotation.MqttClientSubscribe;
import org.dromara.mica.mqtt.core.deserialize.MqttDeserializer;

import java.lang.reflect.Method;

/**
 * {@link MqttClientSubscribe} 解析后的原生订阅定义。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
public class MicaMqttClientSubscribeDefinition {

    private final Object bean;
    private final String beanName;
    private final Method method;
    private final String[] topicTemplates;
    private final String[] topicFilters;
    private final MqttQoS qos;
    private final String clientTemplateBean;
    private final Class<? extends MqttDeserializer> deserializerType;
    private final boolean classLevelListener;

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

    public Object getBean() {
        return bean;
    }

    public String getBeanName() {
        return beanName;
    }

    public Method getMethod() {
        return method;
    }

    public String[] getTopicTemplates() {
        return topicTemplates;
    }

    public String[] getTopicFilters() {
        return topicFilters;
    }

    public MqttQoS getQos() {
        return qos;
    }

    public String getClientTemplateBean() {
        return clientTemplateBean;
    }

    public Class<? extends MqttDeserializer> getDeserializerType() {
        return deserializerType;
    }

    public boolean isClassLevelListener() {
        return classLevelListener;
    }

    /**
     * 从方法级注解构建定义。
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
