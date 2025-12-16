package io.ddd4j.boot.cmpt.express.application.dto;

/**
 * 验证规则请求DTO
 */
public class ValidateRuleRequest {
    private String expression;

    public ValidateRuleRequest() {
    }

    public ValidateRuleRequest(String expression) {
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }
}

