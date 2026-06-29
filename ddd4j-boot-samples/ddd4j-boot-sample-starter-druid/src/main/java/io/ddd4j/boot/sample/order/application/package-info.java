/**
 * 应用层（Application Layer）
 *
 * <p>应用层是DDD分层架构中的协调层，负责协调领域对象完成业务用例。
 * 应用层不包含业务逻辑，只负责编排和事务管理。</p>
 *
 * <h3>主要职责：</h3>
 * <ul>
 *   <li><b>命令（Command）</b>：封装写操作的输入参数，遵循CQRS模式</li>
 *   <li><b>查询（Query）</b>：封装读操作的查询参数</li>
 *   <li><b>响应（Response）</b>：封装读操作的返回结果</li>
 *   <li><b>DTO（Data Transfer Object）</b>：数据传输对象，用于应用层与接口层之间的数据传输</li>
 *   <li><b>应用服务（Application Service）</b>：编排领域对象，处理业务用例</li>
 *   <li><b>映射器（Mapper）</b>：领域对象与DTO之间的转换</li>
 * </ul>
 *
 * <h3>目录结构：</h3>
 * <ul>
 *   <li><code>command/</code> - 命令对象（CQRS中的Command）</li>
 *   <li><code>query/</code> - 查询参数对象（CQRS中的Query）</li>
 *   <li><code>response/</code> - 响应对象</li>
 *   <li><code>dto/</code> - 数据传输对象</li>
 *   <li><code>service/</code> - 应用服务</li>
 *   <li><code>mapper/</code> - 对象映射器</li>
 * </ul>
 *
 * <h3>设计原则：</h3>
 * <ul>
 *   <li>应用服务应该是无状态的</li>
 *   <li>应用服务应该很薄，只负责编排</li>
 *   <li>业务逻辑应该在领域层，而不是应用层</li>
 *   <li>应用服务负责事务边界</li>
 * </ul>
 *
 * @author DDD4J
 * @since 1.0.0
 */
package io.ddd4j.boot.sample.order.application;

