package io.ddd4j.boot.core.contract;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.ddd4j.boot.core.context.BaseContext;
import io.ddd4j.boot.core.context.SpringContext;
import io.ddd4j.boot.core.context.ThreadContext;
import io.ddd4j.boot.core.contract.constant.ContextConstants;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.function.Consumer;

/**
 * MQ事件基类
 */
@Data
public class MQEvent implements Serializable {
    // 消息ID，默认当前时间戳
    protected String msgId;
    // 命名空间
    private String namespace;
    // 主题，配置base-mq.default-topic后无须每次指定
    protected String topic;
    // 标签，只支持单个标签，多标签需要分开发送
    protected String tag;
    // namespace、topic、tag拼接符
    protected String concat;
    // 租户ID，默认从线程上下文获取（外部系统 JSON 常用 tenant_id）
    @JsonAlias("tenant_id")
    protected String tenantId;

    // 策略匹配，supports参数来源于@MQEventListener.supports
    public boolean supports(List<String> supports) {
        return supports.contains(match());
    }

    // 策略匹配项
    public String match() {
        return "*";
    }

    // 发布MQ事件
    public void publish() {
        this.publish(getTopic(), getTag(), getTenantId());
    }

    // 发布MQ事件
    public void publish(String topic) {
        this.publish(topic, getTag(), getTenantId());
    }

    // 发布MQ事件
    public void publish(String topic, String tag) {
        this.publish(topic, tag, getTenantId());
    }

    // 发布MQ事件
    public void publish(String topic, String tag, String tenantId) {
        setTopic(topic);
        if (getTopic() == null) {
            setTopic(SpringContext.getEnv().getProperty("base-mq.default-topic", "DEFAULT"));
        }
        setTag(tag);
        setTenantId(tenantId != null ? tenantId : ThreadContext.get(ContextConstants.TENANT_ID));
        setMsgId(getMsgId() == null ? String.valueOf(System.currentTimeMillis()) : getMsgId());
        // 这里的BaseContext是在基础框架全局使用的上下文，用于解耦各个组件具体的技术代码
        if (BaseContext.contains("MQEventPublisher")) {
            BaseContext.<String, Consumer<MQEvent>>get("MQEventPublisher").accept(this);
        }
    }

    public <T extends MQEvent> T tenantId(String tenantId) {
        this.tenantId = tenantId;
        return (T) this;
    }

}