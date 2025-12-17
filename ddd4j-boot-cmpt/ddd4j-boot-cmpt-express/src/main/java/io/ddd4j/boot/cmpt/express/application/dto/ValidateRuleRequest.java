package io.ddd4j.boot.cmpt.express.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 验证规则请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidateRuleRequest {

    private String expression;

}