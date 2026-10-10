package io.ddd4j.boot.qlexpress.rule;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Boot 规则管理使用的可持久化规则定义。
 *
 * <p>实现 {@link Serializable} 以便随规则仓库持久化与网络传输，非瞬态字段均进入序列化表单。
 *
 * <p>提供 {@link #builder()} 流式构建入口，{@link Builder} 各链式方法与本类字段一一对应。
 */
public class RuleDefinition implements Serializable {

    /** 序列化版本号，字段结构变更时需同步升级。 */
    private static final long serialVersionUID = 1L;

    /** 规则唯一标识（主键）。 */
    private String id;
    /** 规则业务编码，用于引用与去重。 */
    private String code;
    /** 规则名称，展示用。 */
    private String name;
    /** 规则表达式（QLExpress 脚本正文）。 */
    private String expression;
    /** 规则说明，描述用途与约束。 */
    private String description;
    /** 规则分类。 */
    private RuleType type;
    /** 是否启用，默认启用。 */
    private Boolean enabled = true;
    /** 优先级，数值越大越先匹配，默认 0。 */
    private Integer priority = 0;
    /** 创建时间。 */
    private LocalDateTime createdAt;
    /** 最后更新时间。 */
    private LocalDateTime updatedAt;

    /**
     * 无参构造器，供序列化框架与 ORM 反射实例化使用。
     */
    public RuleDefinition() {
    }

    /**
     * 全量字段构造器，按字段声明顺序逐一赋值。
     *
     * @param id          规则唯一标识
     * @param code        规则业务编码
     * @param name        规则名称
     * @param expression  规则表达式
     * @param description 规则说明
     * @param type        规则分类
     * @param enabled     是否启用
     * @param priority    优先级
     * @param createdAt   创建时间
     * @param updatedAt   最后更新时间
     */
    public RuleDefinition(String id, String code, String name, String expression, String description,
                          RuleType type, Boolean enabled, Integer priority,
                          LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.expression = expression;
        this.description = description;
        this.type = type;
        this.enabled = enabled;
        this.priority = priority;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** 获取规则唯一标识。
     * @return 规则唯一标识 */
    public String getId() { return id; }
    /** 设置规则唯一标识。
     * @param id 规则唯一标识 */
    public void setId(String id) { this.id = id; }
    /** 获取规则业务编码。
     * @return 规则业务编码 */
    public String getCode() { return code; }
    /** 设置规则业务编码。
     * @param code 规则业务编码 */
    public void setCode(String code) { this.code = code; }
    /** 获取规则名称。
     * @return 规则名称 */
    public String getName() { return name; }
    /** 设置规则名称。
     * @param name 规则名称 */
    public void setName(String name) { this.name = name; }
    /** 获取规则表达式。
     * @return 规则表达式 */
    public String getExpression() { return expression; }
    /** 设置规则表达式。
     * @param expression 规则表达式 */
    public void setExpression(String expression) { this.expression = expression; }
    /** 获取规则说明。
     * @return 规则说明 */
    public String getDescription() { return description; }
    /** 设置规则说明。
     * @param description 规则说明 */
    public void setDescription(String description) { this.description = description; }
    /** 获取规则分类。
     * @return 规则分类 */
    public RuleType getType() { return type; }
    /** 设置规则分类。
     * @param type 规则分类 */
    public void setType(RuleType type) { this.type = type; }
    /** 获取是否启用。
     * @return 是否启用 */
    public Boolean getEnabled() { return enabled; }
    /** 设置是否启用。
     * @param enabled 是否启用 */
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
    /** 获取优先级。
     * @return 优先级 */
    public Integer getPriority() { return priority; }
    /** 设置优先级。
     * @param priority 优先级 */
    public void setPriority(Integer priority) { this.priority = priority; }
    /** 获取创建时间。
     * @return 创建时间 */
    public LocalDateTime getCreatedAt() { return createdAt; }
    /** 设置创建时间。
     * @param createdAt 创建时间 */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    /** 获取最后更新时间。
     * @return 最后更新时间 */
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    /** 设置最后更新时间。
     * @param updatedAt 最后更新时间 */
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    /**
     * 创建流式构建器，链式设置字段后调用 {@link Builder#build()} 完成组装。
     *
     * @return 新建的规则定义构建器
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * {@link RuleDefinition} 的流式构建器，链式方法与主类字段一一对应。
     */
    public static class Builder {

        /**
         * 无参构造器，配合各链式方法逐步填充字段。
         */
        public Builder() {
        }

        private String id;
        private String code;
        private String name;
        private String expression;
        private String description;
        private RuleType type;
        private Boolean enabled = true;
        private Integer priority = 0;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        /** 设置规则唯一标识。
         * @param value 规则唯一标识
         * @return 当前构建器 */
        public Builder id(String value) { id = value; return this; }
        /** 设置规则业务编码。
         * @param value 规则业务编码
         * @return 当前构建器 */
        public Builder code(String value) { code = value; return this; }
        /** 设置规则名称。
         * @param value 规则名称
         * @return 当前构建器 */
        public Builder name(String value) { name = value; return this; }
        /** 设置规则表达式。
         * @param value 规则表达式
         * @return 当前构建器 */
        public Builder expression(String value) { expression = value; return this; }
        /** 设置规则说明。
         * @param value 规则说明
         * @return 当前构建器 */
        public Builder description(String value) { description = value; return this; }
        /** 设置规则分类。
         * @param value 规则分类
         * @return 当前构建器 */
        public Builder type(RuleType value) { type = value; return this; }
        /** 设置是否启用。
         * @param value 是否启用
         * @return 当前构建器 */
        public Builder enabled(Boolean value) { enabled = value; return this; }
        /** 设置优先级。
         * @param value 优先级
         * @return 当前构建器 */
        public Builder priority(Integer value) { priority = value; return this; }
        /** 设置创建时间。
         * @param value 创建时间
         * @return 当前构建器 */
        public Builder createdAt(LocalDateTime value) { createdAt = value; return this; }
        /** 设置最后更新时间。
         * @param value 最后更新时间
         * @return 当前构建器 */
        public Builder updatedAt(LocalDateTime value) { updatedAt = value; return this; }
        /**
         * 按当前构建器状态组装规则定义实例。
         *
         * @return 新建的规则定义
         */
        public RuleDefinition build() { return new RuleDefinition(id, code, name, expression, description, type, enabled, priority, createdAt, updatedAt); }
    }

    /**
     * 判断规则当前是否可用（已启用）。
     *
     * @return {@code true} 表示规则已启用可用
     */
    public boolean isAvailable() {
        return Boolean.TRUE.equals(enabled);
    }
}
