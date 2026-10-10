package io.ddd4j.boot.mq.mqttmica.bridge.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 存储由桥接扫描器发现的 {@link org.dromara.mica.mqtt.core.annotation.MqttClientSubscribe} 定义。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
public class MicaMqttClientSubscribeDefinitionRegistry {

    private final List<MicaMqttClientSubscribeDefinition> definitions = new ArrayList<>();

    /**
     * 显式无参构造器，供 Spring 实例化为 Bean。
     */
    public MicaMqttClientSubscribeDefinitionRegistry() {
    }

    /**
     * 登记一条原生订阅定义。
     *
     * @param definition 由扫描器解析出的订阅定义
     */
    public void register(MicaMqttClientSubscribeDefinition definition) {
        definitions.add(definition);
    }

    /**
     * 返回已登记定义（只读副本）。
     *
     * @return 不可修改的订阅定义列表
     */
    public List<MicaMqttClientSubscribeDefinition> definitions() {
        return Collections.unmodifiableList(definitions);
    }
}
