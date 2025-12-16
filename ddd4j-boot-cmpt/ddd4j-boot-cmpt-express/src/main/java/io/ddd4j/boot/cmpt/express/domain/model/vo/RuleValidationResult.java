package io.ddd4j.boot.cmpt.express.domain.model.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * 规则验证结果值对象
 * 不可变的值对象，表示规则语法验证的结果
 */
public class RuleValidationResult {

    private final boolean valid;
    private final String message;
    private final List<String> errors;

    private RuleValidationResult(Builder builder) {
        this.valid = builder.valid;
        this.message = builder.message;
        this.errors = builder.errors != null ? new ArrayList<>(builder.errors) : new ArrayList<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean isValid() {
        return valid;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getErrors() {
        return new ArrayList<>(errors);
    }

    public static class Builder {
        private boolean valid;
        private String message;
        private List<String> errors;

        public Builder valid(boolean valid) {
            this.valid = valid;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder errors(List<String> errors) {
            this.errors = errors;
            return this;
        }

        public Builder addError(String error) {
            if (this.errors == null) {
                this.errors = new ArrayList<>();
            }
            this.errors.add(error);
            return this;
        }

        public RuleValidationResult build() {
            return new RuleValidationResult(this);
        }
    }
}

