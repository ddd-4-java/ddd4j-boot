package io.ddd4j.boot.cmpt.express.interfaces.web;

import io.ddd4j.boot.cmpt.express.application.dto.TestRuleRequest;
import io.ddd4j.boot.cmpt.express.application.dto.ValidateRuleRequest;
import io.ddd4j.boot.cmpt.express.application.service.RuleEngineApplicationService;
import io.ddd4j.boot.cmpt.express.application.service.RuleManagementService;
import io.ddd4j.boot.cmpt.express.domain.model.entity.RuleDefinition;
import io.ddd4j.boot.cmpt.express.domain.model.vo.RuleExecutionResult;
import io.ddd4j.boot.cmpt.express.domain.model.vo.RuleValidationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 规则管理Controller
 * 接口层：提供规则管理的REST API
 */
@RestController
@RequestMapping("/api/rules")
@Slf4j
public class RuleManagementController {

    @Autowired
    private RuleEngineApplicationService ruleEngineApplicationService;

    @Autowired
    private RuleManagementService ruleManagementService;

    /**
     * 查询规则列表
     */
    @GetMapping
    public ResponseEntity<List<RuleDefinition>> listRules(
            @RequestParam(required = false) String ruleType) {
        List<RuleDefinition> rules;
        if (StringUtils.hasText(ruleType)) {
            rules = ruleManagementService.getRulesByType(ruleType);
        } else {
            rules = ruleManagementService.getAllRules();
        }
        return ResponseEntity.ok(rules);
    }

    /**
     * 获取规则详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<RuleDefinition> getRule(@PathVariable Long id) {
        Optional<RuleDefinition> rule = ruleManagementService.getRuleById(id);
        return rule.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 根据规则编码获取规则详情（带缓存）
     */
    @GetMapping("/code/{ruleCode}")
    public ResponseEntity<RuleDefinition> getRuleByCode(@PathVariable String ruleCode) {
        Optional<RuleDefinition> rule = ruleManagementService.getRuleByCode(ruleCode);
        return rule.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 创建规则
     * 自动验证规则语法，保存到数据库，并同步更新缓存
     */
    @PostMapping
    public ResponseEntity<RuleDefinition> createRule(@RequestBody RuleDefinition rule) {
        RuleDefinition savedRule = ruleManagementService.createRule(rule);
        return ResponseEntity.ok(savedRule);
    }

    /**
     * 更新规则
     * 自动验证规则语法，更新数据库，并同步更新缓存
     */
    @PutMapping("/{id}")
    public ResponseEntity<RuleDefinition> updateRule(@PathVariable Long id, @RequestBody RuleDefinition rule) {
        try {
            RuleDefinition updatedRule = ruleManagementService.updateRule(id, rule);
            return ResponseEntity.ok(updatedRule);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 删除规则
     * 自动删除数据库记录，并同步清除缓存
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable Long id) {
        try {
            ruleManagementService.deleteRule(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 启用规则
     * 同步更新缓存
     */
    @PostMapping("/{id}/enable")
    public ResponseEntity<RuleDefinition> enableRule(@PathVariable Long id) {
        try {
            RuleDefinition rule = ruleManagementService.enableRule(id);
            return ResponseEntity.ok(rule);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 禁用规则
     * 同步清除缓存
     */
    @PostMapping("/{id}/disable")
    public ResponseEntity<RuleDefinition> disableRule(@PathVariable Long id) {
        try {
            RuleDefinition rule = ruleManagementService.disableRule(id);
            return ResponseEntity.ok(rule);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 测试规则
     */
    @PostMapping("/test")
    public ResponseEntity<RuleExecutionResult> testRule(@RequestBody TestRuleRequest request) {
        RuleExecutionResult result = ruleEngineApplicationService.executeRule(
            request.getRuleCode(), request.getContext());
        return ResponseEntity.ok(result);
    }

    /**
     * 验证规则语法
     */
    @PostMapping("/validate")
    public ResponseEntity<RuleValidationResult> validateRule(@RequestBody ValidateRuleRequest request) {
        RuleValidationResult result = ruleEngineApplicationService.validateRule(request.getExpression());
        return ResponseEntity.ok(result);
    }

    /**
     * 清除指定规则缓存
     */
    @PostMapping("/cache/clear/{ruleCode}")
    public ResponseEntity<String> clearRuleCache(@PathVariable String ruleCode) {
        ruleManagementService.clearRuleCache(ruleCode);
        return ResponseEntity.ok("规则缓存清除成功: " + ruleCode);
    }

    /**
     * 清除所有规则缓存
     */
    @PostMapping("/cache/clear")
    public ResponseEntity<String> clearAllCache() {
        ruleManagementService.clearAllRuleCache();
        return ResponseEntity.ok("所有规则缓存清除成功");
    }
}