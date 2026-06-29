/**
 * ddd4j-boot DDD 注解包
 *
 * <p>11 个 DDD 构造型注解的同名复制 + Spring 元注解融合实现。
 * 每个注解都是 {@link io.ddd4j.annotation.ddd.DDDAnnotation} 元注解标记，
 * 同时底层融合 Spring 框架的对应原生注解（{@code @Service} / {@code @Repository} /
 * {@code @Component}），实现"业务代码只写一个注解"。
 *
 * <p>注意：{@link io.ddd4j.annotation.ddd.DomainEvent} 不在此包内——它是纯 marker，
 * 事件对象通过 {@link io.ddd4j.core.contract.DomainEventPublisher} 发布，
 * 不需要注册为 Bean。
 *
 * @see io.ddd4j.annotation.ddd.DDDAnnotation
 * @see <a href="https://github.com/partme-ai/ddd4j/blob/master/docs/architecture/annotation-architecture.md">annotation-architecture.md</a>
 */
package io.ddd4j.boot.annotation.ddd;
