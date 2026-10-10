package io.ddd4j.boot.sample.domain.order.model.aggregate;

import io.ddd4j.boot.sample.domain.order.event.DomainEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * 领域事件发布器（聚合根内部使用）
 *
 * <p>领域语义：聚合根在执行行为方法时并不直接对外发消息，而是把产生的
 * 领域事件暂存到本发布器中（事务提交后再由基础设施层真正投递），
 * 以此保证「聚合状态变更」与「事件记录」的原子性。</p>
 */
public class DomainEventPublisher {

    /** 暂存的领域事件，按产生顺序排列。 */
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    /**
     * 构造空的事件发布器，暂存区初始为空。
     */
    public DomainEventPublisher() {
    }

    /**
     * 发布（暂存）领域事件。
     *
     * <p>{@code event} 为 {@code null} 时忽略，不入暂存区。</p>
     *
     * @param event 待暂存的领域事件，可为 {@code null}
     */
    public void publish(DomainEvent event) {
        if (event != null) {
            domainEvents.add(event);
        }
    }

    /**
     * 获取所有领域事件。
     *
     * @return 暂存事件列表的防御性副本，外部修改不会影响发布器内部状态
     */
    public List<DomainEvent> getDomainEvents() {
        return new ArrayList<>(domainEvents);
    }

    /**
     * 清空领域事件。
     *
     * <p>通常在事件完成投递后由调用方调用，防止同一批事件被重复发布。</p>
     */
    public void clear() {
        domainEvents.clear();
    }
}
