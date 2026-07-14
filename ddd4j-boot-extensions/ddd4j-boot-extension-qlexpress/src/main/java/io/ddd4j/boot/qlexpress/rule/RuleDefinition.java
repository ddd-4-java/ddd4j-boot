package io.ddd4j.boot.qlexpress.rule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Boot 规则管理使用的可持久化规则定义。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleDefinition implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String code;
    private String name;
    private String expression;
    private String description;
    private RuleType type;
    @Builder.Default
    private Boolean enabled = true;
    @Builder.Default
    private Integer priority = 0;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public boolean isAvailable() {
        return Boolean.TRUE.equals(enabled);
    }
}
