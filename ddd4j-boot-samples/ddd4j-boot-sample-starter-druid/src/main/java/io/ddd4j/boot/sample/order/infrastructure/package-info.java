/**
 * 基础设施层（Infrastructure Layer）
 *
 * <p>基础设施层提供技术实现，包括持久化、消息传递、外部服务调用等。
 * 基础设施层实现领域层定义的接口，但不影响领域层的设计。</p>
 *
 * <b>主要职责：</b>
 * <ul>
 *   <li><b>持久化实现（Persistence）</b>：实现仓储接口，提供数据持久化能力</li>
 *   <li><b>消息处理（Messaging）</b>：处理领域事件的发布和订阅</li>
 *   <li><b>配置（Config）</b>：技术相关的配置类</li>
 * </ul>
 *
 * <b>目录结构：</b>
 * <ul>
 *   <li><code>persistence/</code> - 持久化实现
 *     <ul>
 *       <li><code>entity/</code> - 持久化实体（JPA/MyBatis实体）</li>
 *       <li><code>mapper/</code> - 数据访问层（MyBatis Mapper）</li>
 *       <li><code>converter/</code> - 领域对象与持久化实体的转换器</li>
 *     </ul>
 *   </li>
 *   <li><code>messaging/</code> - 消息处理
 *     <ul>
 *       <li><code>handler/</code> - 领域事件处理器</li>
 *     </ul>
 *   </li>
 *   <li><code>config/</code> - 配置类</li>
 * </ul>
 *
 * <b>设计原则：</b>
 * <ul>
 *   <li>基础设施层实现领域层定义的接口</li>
 *   <li>领域对象与持久化实体分离</li>
 *   <li>使用转换器进行对象转换</li>
 *   <li>技术细节不应该泄露到领域层</li>
 * </ul>
 *
 * @author DDD4J
 * @since 1.0.0
 */
package io.ddd4j.boot.sample.order.infrastructure;

