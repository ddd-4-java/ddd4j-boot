package io.ddd4j.boot.cmpt.express.application.dto;

import java.util.Map;

/**
 * 测试规则请求DTO
 */
public class TestRuleRequest {
    private String ruleCode;
    private Map<String, Object> context;

    public TestRuleRequest() {
    }

    public TestRuleRequest(String ruleCode, Map<String, Object> context) {
        this.ruleCode = ruleCode;
        this.context = context;
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }

    public Map<String, Object> getContext() {
        return context;
    }

    public void setContext(Map<String, Object> context) {
        this.context = context;
    }
}

