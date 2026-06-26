package io.ddd4j.ddd.event;

import lombok.Getter;
import org.fuin.ddd4j.core.EventId;

import java.time.Instant;

/**
 * Ddd4j 领域事件基类（轻量版，兼容 fuinorg 0.7.0 API）。
 *
 * <p>对齐 3.4.x 的 ddd4j-boot-ddd / DddDomainEvent。
 *
 * <p>注：0.7.0 版本的 fuinorg ddd-4-java-core 移除了 {@code AbstractDomainEvent} 抽象基类，
 * 本实现直接提供一个可序列化的领域事件基类，业务事件继承后自动获得 eventId/occurredAt。
 *
 * <p>业务领域事件应继承本类：
 * <pre>{@code
 * public class OrderPlacedEvent extends DddDomainEvent {
 *     // 业务字段
 * }
 * }</pre>
 */
@Getter
public abstract class DddDomainEvent implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 事件唯一 ID。
     */
    private final EventId eventId;

    /**
     * 事件发生时间。
     */
    private final Instant occurredAt;

    protected DddDomainEvent() {
        this.eventId = new EventId();
        this.occurredAt = Instant.now();
    }
}
