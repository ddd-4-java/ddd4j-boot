package io.ddd4j.boot.mq.core;

import io.ddd4j.boot.core.contract.MQEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Function;

/**
 * MQ监听器
 *
 * @deprecated 请使用 {@link io.ddd4j.boot.mq.registry.MQListenerDefinition}。
 */
@Deprecated
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MQListener {
    // 监听器方法
    private Method method;
    // MQ事件反序列化函数
    private Function<String, MQEvent> deserialize;
    // 消费者组
    private String group;
    // 命名空间
    private String namespace;
    // 主题
    private String topic;
    // 标签列表
    private String tags;
    // 拼接符
    private String concat;
    // 支持消费的列表
    private List<String> supports;
    // 是否监听成功
    private boolean success;

    public String namespaceTopicTags() {
        if (tags == null || tags.isEmpty()) {
            return namespace + concat + topic;
        }
        return namespace + concat + topic + concat + tags;
    }

    public String group(String concat) {
        if (this.group != null && !this.group.isEmpty()) {
            return group + concat;
        }
        return "";
    }

    public String tags(String concat) {
        if (this.tags != null && !this.tags.isEmpty()) {
            return concat + tags;
        }
        return "";
    }

    public String getTags() {
        return tags == null ? "" : tags;
    }

}