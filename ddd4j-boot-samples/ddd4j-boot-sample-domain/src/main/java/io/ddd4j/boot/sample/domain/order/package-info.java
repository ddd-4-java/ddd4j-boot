/**
 * 领域层（Domain Layer）
 * 
 * <p>领域层是DDD分层架构的核心层，包含业务逻辑和领域模型。
 * 领域层应该是技术无关的，不依赖任何基础设施。</p>
 * 
 * <h3>主要职责：</h3>
 * <ul>
 *   <li><b>聚合根（Aggregate Root）</b>：维护聚合的一致性边界，封装业务逻辑</li>
 *   <li><b>实体（Entity）</b>：具有唯一标识的对象</li>
 *   <li><b>值对象（Value Object）</b>：不可变的对象，通过值相等性判断</li>
 *   <li><b>领域服务（Domain Service）</b>：处理跨聚合的业务逻辑</li>
 *   <li><b>仓储接口（Repository Interface）</b>：定义数据访问接口，不包含实现</li>
 *   <li><b>领域事件（Domain Event）</b>：捕获领域中的重要事件</li>
 *   <li><b>规格（Specification）</b>：封装复杂的业务规则</li>
 * </ul>
 * 
 * <h3>目录结构：</h3>
 * <ul>
 *   <li><code>model/</code> - 领域模型
 *     <ul>
 *       <li><code>aggregate/</code> - 聚合根</li>
 *       <li><code>entity/</code> - 实体</li>
 *       <li><code>vo/</code> - 值对象</li>
 *     </ul>
 *   </li>
 *   <li><code>repository/</code> - 仓储接口</li>
 *   <li><code>service/</code> - 领域服务</li>
 *   <li><code>event/</code> - 领域事件</li>
 *   <li><code>specification/</code> - 规格模式</li>
 * </ul>
 * 
 * <h3>设计原则：</h3>
 * <ul>
 *   <li>领域层应该是技术无关的</li>
 *   <li>业务逻辑应该在领域对象中</li>
 *   <li>聚合根负责维护聚合的一致性</li>
 *   <li>值对象应该是不可变的</li>
 *   <li>领域事件用于解耦和扩展</li>
 * </ul>
 * 
 * @author DDD4J
 * @since 1.0.0
 */
package io.ddd4j.boot.sample.domain.order;

