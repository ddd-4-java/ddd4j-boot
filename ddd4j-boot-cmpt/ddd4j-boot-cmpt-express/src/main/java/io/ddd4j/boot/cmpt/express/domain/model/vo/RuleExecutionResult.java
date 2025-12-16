package io.ddd4j.boot.cmpt.express.domain.model.vo;

import java.time.LocalDateTime;

/**
 * 规则执行结果值对象
 * 不可变的值对象，表示规则执行的结果
 */
public class RuleExecutionResult {

    private final boolean success;
    private final String errorCode;
    private final String errorMessage;
    private final Object result;
    private final String ruleCode;
    private final LocalDateTime executedAt;
    private final long executionTime; // 执行耗时（毫秒）

    private RuleExecutionResult(Builder builder) {
        this.success = builder.success;
        this.errorCode = builder.errorCode;
        this.errorMessage = builder.errorMessage;
        this.result = builder.result;
        this.ruleCode = builder.ruleCode;
        this.executedAt = builder.executedAt != null ? builder.executedAt : LocalDateTime.now();
        this.executionTime = builder.executionTime;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Object getResult() {
        return result;
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public LocalDateTime getExecutedAt() {
        return executedAt;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public static class Builder {
        private boolean success;
        private String errorCode;
        private String errorMessage;
        private Object result;
        private String ruleCode;
        private LocalDateTime executedAt;
        private long executionTime;

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public Builder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public Builder result(Object result) {
            this.result = result;
            return this;
        }

        public Builder ruleCode(String ruleCode) {
            this.ruleCode = ruleCode;
            return this;
        }

        public Builder executedAt(LocalDateTime executedAt) {
            this.executedAt = executedAt;
            return this;
        }

        public Builder executionTime(long executionTime) {
            this.executionTime = executionTime;
            return this;
        }

        public RuleExecutionResult build() {
            return new RuleExecutionResult(this);
        }
    }
}

