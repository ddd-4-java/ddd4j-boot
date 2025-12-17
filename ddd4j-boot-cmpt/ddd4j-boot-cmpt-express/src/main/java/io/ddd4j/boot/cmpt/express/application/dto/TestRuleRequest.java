package io.ddd4j.boot.cmpt.express.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 测试规则请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestRuleRequest {

    private String ruleCode;
    private Map<String, Object> context;

}

