package io.ddd4j.boot.core.contract;

import java.io.Serializable;

/**
 * 命令契约（CQRS 写侧）。
 *
 * <p>命令是"意图/请求"的载体，表达"用户希望系统做什么"。与查询（{@link Query}）分离，
 * 遵循 CQRS 原则。命令的执行会产生领域事件和状态变更。
 *
 * <p>本接口是轻量标记接口（marker interface），不依赖 fuinorg。
 * 事件溯源场景的完整命令契约在 {@code ddd4j-boot-ddd} 模块（基于 cqrs-4-java）。
 *
 * <p>使用方式：
 * <pre>
 * public class CreateOrderCommand implements Command {
 *     private final String userId;
 *     private final Money total;
 *     // constructor + getters
 * }
 *
 * // 命令处理器
 * public class CreateOrderHandler {
 *     public Order handle(CreateOrderCommand cmd) {
 *         Order order = new Order(...);
 *         return orderRepository.save(order);
 *     }
 * }
 * </pre>
 *
 * @author wandl
 * @since 3.4.x
 * @see Query
 */
public interface Command extends Serializable {
}
