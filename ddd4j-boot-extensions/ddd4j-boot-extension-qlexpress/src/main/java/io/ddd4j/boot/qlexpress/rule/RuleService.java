package io.ddd4j.boot.qlexpress.rule;

import io.ddd4j.extension.qlexpress.QLExpressEngine;
import io.ddd4j.extension.qlexpress.model.QLExpressExecutionResult;
import io.ddd4j.extension.qlexpress.model.QLExpressValidationResult;
import io.ddd4j.kit.lang.StrKit;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Boot 层规则 CRUD、缓存协调、执行和事件发布服务。
 */
public class RuleService {

    private final RuleRepository repository;
    private final RuleCache cache;
    private final QLExpressEngine engine;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 构造规则服务，四个依赖均做非空校验，缺失时立即失败。
     *
     * @param repository     规则持久化仓库
     * @param cache          规则缓存
     * @param engine         QLExpress 执行引擎
     * @param eventPublisher Spring 事件发布器，用于广播规则变更事件
     */
    public RuleService(RuleRepository repository, RuleCache cache, QLExpressEngine engine,
                       ApplicationEventPublisher eventPublisher) {
        this.repository = Objects.requireNonNull(repository, "repository 不能为空");
        this.cache = Objects.requireNonNull(cache, "cache 不能为空");
        this.engine = Objects.requireNonNull(engine, "engine 不能为空");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "eventPublisher 不能为空");
    }

    /**
     * 新建规则：校验合法性与编码唯一性，生成主键与时间戳，落库后写缓存并发布 CREATED 事件。
     *
     * @param rule 待创建的规则定义
     * @return 持久化后的规则定义
     * @throws IllegalArgumentException 编码已存在或规则必填项校验失败
     */
    public RuleDefinition create(RuleDefinition rule) {
        RuleDefinition checked = requireValidRule(rule);
        if (repository.findByCode(checked.getCode()).isPresent()) {
            throw new IllegalArgumentException("规则编码已存在: " + checked.getCode());
        }
        LocalDateTime now = LocalDateTime.now();
        if (!StrKit.hasText(checked.getId())) {
            checked.setId(UUID.randomUUID().toString());
        }
        checked.setCreatedAt(now);
        checked.setUpdatedAt(now);
        RuleDefinition saved = repository.save(checked);
        cache.put(saved.getCode(), saved);
        publish(saved, RuleChangedEvent.Operation.CREATED);
        return saved;
    }

    /**
     * 更新规则：编码不可变，仅合并可变字段（名称、表达式、说明、分类、启用状态、优先级），
     * 更新后刷新缓存并发布 UPDATED 事件。
     *
     * @param id      规则唯一标识
     * @param changes 变更内容（按字段覆盖）
     * @return 更新后的规则定义
     * @throws IllegalArgumentException 规则不存在、尝试修改编码或校验失败
     */
    public RuleDefinition update(String id, RuleDefinition changes) {
        requireText(id, "id");
        RuleDefinition existing = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("规则不存在: " + id));
        RuleDefinition checked = Objects.requireNonNull(changes, "changes 不能为空");
        if (StrKit.hasText(checked.getCode()) && !Objects.equals(existing.getCode(), checked.getCode())) {
            throw new IllegalArgumentException("规则编码不允许修改: " + existing.getCode());
        }
        checked.setCode(existing.getCode());
        requireValidRule(checked);
        existing.setName(checked.getName());
        existing.setExpression(checked.getExpression());
        existing.setDescription(checked.getDescription());
        existing.setType(checked.getType());
        existing.setEnabled(Objects.nonNull(checked.getEnabled()) ? checked.getEnabled() : existing.getEnabled());
        existing.setPriority(Objects.nonNull(checked.getPriority()) ? checked.getPriority() : existing.getPriority());
        existing.setUpdatedAt(LocalDateTime.now());
        RuleDefinition saved = repository.save(existing);
        cache.put(saved.getCode(), saved);
        publish(saved, RuleChangedEvent.Operation.UPDATED);
        return saved;
    }

    /**
     * 删除规则：落库删除后清理对应缓存并发布 DELETED 事件。
     *
     * @param id 规则唯一标识
     * @throws IllegalArgumentException 规则不存在或 id 为空
     */
    public void delete(String id) {
        requireText(id, "id");
        RuleDefinition existing = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("规则不存在: " + id));
        repository.deleteById(id);
        cache.evict(existing.getCode());
        publish(existing, RuleChangedEvent.Operation.DELETED);
    }

    /**
     * 启用规则：刷新启用状态与时间戳，写入缓存并发布 ENABLED 事件。
     *
     * @param id 规则唯一标识
     * @return 启用后的规则定义
     * @throws IllegalArgumentException 规则不存在或 id 为空
     */
    public RuleDefinition enable(String id) {
        return changeAvailability(id, true, RuleChangedEvent.Operation.ENABLED);
    }

    /**
     * 停用规则：刷新启用状态与时间戳，清理缓存并发布 DISABLED 事件。
     *
     * @param id 规则唯一标识
     * @return 停用后的规则定义
     * @throws IllegalArgumentException 规则不存在或 id 为空
     */
    public RuleDefinition disable(String id) {
        return changeAvailability(id, false, RuleChangedEvent.Operation.DISABLED);
    }

    /**
     * 按唯一标识查询规则，id 为空时直接返回空结果不回源。
     *
     * @param id 规则唯一标识
     * @return 命中的规则定义，未命中时为空
     */
    public Optional<RuleDefinition> findById(String id) {
        if (!StrKit.hasText(id)) {
            return Optional.empty();
        }
        return repository.findById(id);
    }

    /**
     * 按业务编码查询规则：先查缓存，未命中回源仓库并回填缓存。
     *
     * @param code 规则业务编码
     * @return 命中的规则定义，未命中时为空
     * @throws IllegalArgumentException code 为空
     */
    public Optional<RuleDefinition> findByCode(String code) {
        requireText(code, "code");
        RuleDefinition cached = cache.get(code);
        if (Objects.nonNull(cached)) {
            return Optional.of(cached);
        }
        Optional<RuleDefinition> rule = repository.findByCode(code);
        rule.ifPresent(value -> cache.put(code, value));
        return rule;
    }

    /**
     * 查询全部规则并按优先级倒序排列（空优先级按 0 处理）。
     *
     * @return 按优先级降序排列的规则列表
     */
    public List<RuleDefinition> findAll() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(
                        rule -> Objects.nonNull(rule.getPriority()) ? rule.getPriority() : 0,
                        Comparator.reverseOrder()))
                .toList();
    }

    /**
     * 按编码加载规则并执行表达式，规则不存在或已禁用时返回失败结果而非抛异常。
     *
     * @param code    规则业务编码
     * @param context 表达式执行上下文变量
     * @return 执行结果；规则缺失时失败码 {@code RULE_NOT_FOUND}，禁用时为 {@code RULE_DISABLED}
     */
    public QLExpressExecutionResult<Object> execute(String code, Map<String, Object> context) {
        Optional<RuleDefinition> rule = findByCode(code);
        if (rule.isEmpty()) {
            return QLExpressExecutionResult.failure("RULE_NOT_FOUND", "规则不存在: " + code, 0L);
        }
        if (!rule.get().isAvailable()) {
            return QLExpressExecutionResult.failure("RULE_DISABLED", "规则已禁用: " + code, 0L);
        }
        return engine.executeSafely(rule.get().getExpression(), context);
    }

    /**
     * 清空全部规则缓存，用于运维手工刷新或批量变更后的失效兜底。
     */
    public void clearCache() {
        cache.clear();
    }

    private RuleDefinition changeAvailability(String id, boolean enabled,
                                              RuleChangedEvent.Operation operation) {
        requireText(id, "id");
        RuleDefinition rule = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("规则不存在: " + id));
        rule.setEnabled(enabled);
        rule.setUpdatedAt(LocalDateTime.now());
        RuleDefinition saved = repository.save(rule);
        if (enabled) {
            cache.put(saved.getCode(), saved);
        } else {
            cache.evict(saved.getCode());
        }
        publish(saved, operation);
        return saved;
    }

    private RuleDefinition requireValidRule(RuleDefinition rule) {
        RuleDefinition checked = Objects.requireNonNull(rule, "rule 不能为空");
        requireText(checked.getCode(), "rule.code");
        requireText(checked.getName(), "rule.name");
        requireText(checked.getExpression(), "rule.expression");
        Objects.requireNonNull(checked.getType(), "rule.type 不能为空");
        QLExpressValidationResult validation = engine.validate(checked.getExpression());
        if (!validation.valid()) {
            throw new IllegalArgumentException("规则表达式无效: " + validation.message());
        }
        return checked;
    }

    private void publish(RuleDefinition rule, RuleChangedEvent.Operation operation) {
        eventPublisher.publishEvent(RuleChangedEvent.of(rule, operation));
    }

    private static void requireText(String value, String field) {
        if (!StrKit.hasText(value)) {
            throw new IllegalArgumentException(field + " 不能为空");
        }
    }
}
