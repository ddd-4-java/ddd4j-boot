/**
 * 接口层（Interfaces Layer / Presentation Layer）
 *
 * <p>接口层是系统与外部交互的边界，负责接收外部请求并返回响应。
 * 接口层不包含业务逻辑，只负责参数验证、调用应用服务和格式化响应。</p>
 *
 * <h3>主要职责：</h3>
 * <ul>
 *   <li><b>REST接口（REST）</b>：提供HTTP RESTful API</li>
 *   <li><b>门面（Facade）</b>：提供外部服务接口（如RPC、消息队列等）</li>
 * </ul>
 *
 * <h3>目录结构：</h3>
 * <ul>
 *   <li><code>rest/</code> - REST接口控制器</li>
 *   <li><code>facade/</code> - 外部服务接口</li>
 * </ul>
 *
 * <h3>设计原则：</h3>
 * <ul>
 *   <li>接口层应该是很薄的，只负责参数验证和调用应用服务</li>
 *   <li>业务逻辑不应该在接口层</li>
 *   <li>使用DTO进行数据传输</li>
 *   <li>统一异常处理</li>
 *   <li>统一响应格式</li>
 * </ul>
 *
 * @author DDD4J
 * @since 1.0.0
 */
package io.ddd4j.boot.sample.order.interfaces;

