/**
 * 订单服务客户端SDK
 * 
 * <p>客户端模块（Client Module）是COLA架构中的接口层，用于定义对外提供的服务接口和DTO。</p>
 * 
 * <h3>主要职责：</h3>
 * <ul>
 *   <li><b>服务接口定义（API）</b>：定义对外提供的服务接口，供其他服务或客户端调用</li>
 *   <li><b>请求对象（Request）</b>：定义客户端调用服务时的请求参数</li>
 *   <li><b>响应对象（Response）</b>：定义服务返回给客户端的数据结构</li>
 * </ul>
 * 
 * <h3>目录结构：</h3>
 * <ul>
 *   <li><code>api/</code> - 服务接口定义（SDK接口）</li>
 *   <li><code>dto/request/</code> - 请求对象</li>
 *   <li><code>dto/response/</code> - 响应对象</li>
 * </ul>
 * 
 * <h3>设计原则：</h3>
 * <ul>
 *   <li>客户端模块应该是独立的，不依赖其他业务模块</li>
 *   <li>使用独立的DTO对象，不暴露内部实现细节</li>
 *   <li>接口定义应该清晰、稳定，便于版本管理</li>
 *   <li>支持多种调用方式（REST、RPC等）</li>
 * </ul>
 * 
 * <h3>使用场景：</h3>
 * <ul>
 *   <li>微服务之间的服务调用</li>
 *   <li>前端或移动端调用后端服务</li>
 *   <li>第三方系统集成</li>
 *   <li>服务间的事件通知</li>
 * </ul>
 * 
 * @author DDD4J
 * @since 1.0.0
 */
package io.ddd4j.boot.sample.client.order;

