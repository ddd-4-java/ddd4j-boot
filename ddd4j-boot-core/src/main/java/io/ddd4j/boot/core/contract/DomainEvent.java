package io.ddd4j.boot.core.contract;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * 纯净领域事件契约（轻量版，不依赖 fuinorg）。
 *
 * <p>这是 ddd4j-boot 的<b>简单领域事件</b>契约，适合不需要事件溯源的场景。
 * 基于 Spring {@code ApplicationEvent} 发布（通过 {@code ApplicationContext.publishEvent}），
 * 用 {@code @EventListener} 监听。
 *
 * <p>与 {@code ddd4j-boot-ddd} 模块的 {@code DddDomainEvent}（基于 fuinorg）的区别：
 *
 * <table border="1">
 *   <tr><th></th><th>本接口（轻量版）</th><th>DddDomainEvent（fuinorg 版）</th></tr>
 *   <tr><td>依赖</td><td>无（纯 Java）</td><td>org.fuin.ddd4j</td></tr>
 *   <tr><td>事件溯源</td><td>不支持</td><td>支持（可持久化到 EventStore）</td></tr>
 *   <tr><td>聚合版本</td><td>无</td><td>有（aggregateVersion）</td></tr>
 *   <tr><td>链路追踪</td><td>仅 eventId</td><td>eventId + correlationId + causationId</td></tr>
 *   <tr><td>适用场景</td><td>简单业务通知</td><td>事件溯源/CQRS/审计</td></tr>
 * </table>
 *
 * <p>使用方式（简单场景，直接用 Spring ApplicationEvent 发布）：
 * <pre>
 * public class UserRegisteredEvent implements DomainEvent {
 *     private final String eventId = UUID.randomUUID().toString();
 *     private final Instant occurredOn = Instant.now();
 *     private final String userId;
 *
 *     public UserRegisteredEvent(String userId) { this.userId = userId; }
 *
 *     // getters...
 * }
 *
 * // 发布
 * applicationContext.publishEvent(new UserRegisteredEvent(userId));
 *
 * // 监听
 * &#64;EventListener
 * public void on(UserRegisteredEvent event) { sendWelcomeEmail(event.getUserId()); }
 * </pre>
 *
 * @author wandl
 * @since 3.4.x
 * @see io.ddd4j.boot.ddd.event.DddDomainEvent 用于事件溯源的完整版
 */
public interface DomainEvent extends Serializable {

    /**
     * 事件唯一标识（UUID）。
     *
     * @return 事件 ID
     */
    String getEventId();

    /**
     * 事件发生时间。
     *
     * @return 发生时间
     */
    Instant getOccurredOn();

}
