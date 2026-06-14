package io.ddd4j.boot.core.contract;

import java.io.Serializable;

/**
 * 查询契约（CQRS 读侧）。
 *
 * <p>查询是"数据获取"的载体，表达"用户希望读取什么数据"。与命令（{@link Command}）分离，
 * 遵循 CQRS 原则。查询<b>不产生</b>状态变更。
 *
 * <p>本接口是轻量标记接口，不依赖任何 ORM 框架。分页/排序参数可通过子接口扩展。
 *
 * <p>使用方式：
 * <pre>
 * public class FindOrderQuery implements Query {
 *     private final String userId;
 *     private final int page;
 *     private final int size;
 *     // constructor + getters
 * }
 *
 * // 查询处理器
 * public class FindOrderHandler {
 *     public Page&lt;Order&gt; handle(FindOrderQuery query) {
 *         return orderRepository.findByUserId(query.getUserId(), query.getPage(), query.getSize());
 *     }
 * }
 * </pre>
 *
 * @author wandl
 * @since 3.4.x
 * @see Command
 */
public interface Query extends Serializable {
}
