/**
 * 框架无关的 DDD 契约包。
 *
 * <p>本包定义领域层的核心抽象，所有类<b>不依赖</b>任何 ORM 框架（MyBatis/JPA）、
 * Web 框架（Servlet/Reactor）或基础设施框架。它们是纯净 DDD 轨道的契约层。
 *
 * <p>与 {@code io.ddd4j.boot.core.entity.BaseEntity}（MyBatis Plus ActiveRecord 轨道）的区别：
 * <ul>
 *   <li>{@link io.ddd4j.boot.core.contract.DomainModel} 不继承任何框架类</li>
 *   <li>{@link io.ddd4j.boot.core.contract.Repository} 是框架无关的仓储接口</li>
 *   <li>适合需要严格领域纯净性的项目（COLA/Clean/Hexagonal 架构）</li>
 * </ul>
 *
 * <p>事件溯源（Event Sourcing）的完整能力在 {@code ddd4j-boot-ddd} 模块，
 * 基于 fuinorg ddd-4-java 的 AbstractAggregateRoot。
 *
 * @author wandl
 * @since 3.4.x
 */
package io.ddd4j.boot.core.contract;
