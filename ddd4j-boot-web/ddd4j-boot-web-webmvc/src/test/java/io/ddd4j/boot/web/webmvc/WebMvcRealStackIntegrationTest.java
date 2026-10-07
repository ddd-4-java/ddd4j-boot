/*
 * Copyright (c) 2024-2026 ddd4j project. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.ddd4j.boot.web.webmvc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * WebMVC 真实 Servlet 栈集成测试（真实 DispatcherServlet + Ddd4jWebMvcInterceptor 全链 + 统一异常翻译）。
 *
 * <p><b>证据性裁定（外部设施面调研）</b>：web 域（javalin/vertx/webflux/webmvc 四模块）为内嵌
 * HTTP 服务器整合层，依赖树中无数据库/缓存/消息代理等外部基础设施，HTTP 服务器本身即进程内组件，
 * 因此 web 域不适用 testcontainers 容器；其最接近的真实集成点是完整 Servlet 管线
 * （DispatcherServlet → ddd4j 拦截器 → 控制器 → 统一异常翻译）的端到端行为。
 *
 * <p><b>传输层说明</b>：受控验证环境的主机安全策略对 Java 进程自身监听的任意端口一律丢弃
 * loopback TCP 连接（已用最小 Socket 探针取证：任意端口自连均超时，仅 Docker 发布端口可达），
 * 故 RANDOM_PORT 真实 Tomcat 方案在受控环境不可验证；此处以 MockMvc 驱动真实 Servlet 管线
 * （仅传输层为 Mock，路由/拦截器/异常翻译链路全部为生产组件），容器真实网络面由 data 域
 * testcontainers IT 覆盖。
 *
 * <p>对应 change: establish-boot-container-it（5.3 web 域）。
 *
 * @since 4.0.x
 */
@DisplayName("WebMVC × 真实 Servlet 管线（拦截器全链 + 统一异常翻译）")
@SpringBootTest(classes = WebMvcRealStackIntegrationTest.ItApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        properties = "ddd4j.web.public-paths=/it/**")
class WebMvcRealStackIntegrationTest {

    /**
     * 真实 Servlet 管线驱动器。
     */
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    /**
     * 以真实 Web 应用上下文装配 Servlet 管线（挂载全部真实 Filter，Boot 4 已移除 @AutoConfigureMockMvc）。
     */
    @BeforeEach
    void setUpMockMvc() {
        jakarta.servlet.Filter[] filters = webApplicationContext
                .getBeansOfType(jakarta.servlet.Filter.class).values().toArray(new jakarta.servlet.Filter[0]);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilters(filters)
                .build();
    }

    @Test
    @DisplayName("正常端点：真实拦截器全链放行并返回业务结果")
    void normalEndpointShouldPassThroughRealInterceptorChain() throws Exception {
        String body = mockMvc.perform(get("/it/ok"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8);
        assertThat(body).as("真实管线必须返回业务结果").contains("it-ok");
    }

    @Test
    @DisplayName("异常端点：IllegalArgumentException 被统一异常翻译为非 2xx 的标准响应体")
    void exceptionEndpointShouldBeTranslatedByRealHandler() throws Exception {
        String body = mockMvc.perform(get("/it/boom"))
                .andExpect(result -> assertThat(result.getResponse().getStatus())
                        .as("业务异常必须被统一翻译为 4xx/5xx，实际 %d", result.getResponse().getStatus())
                        .isBetween(400, 599))
                .andReturn().getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8);
        assertThat(body).as("响应体必须为 ddd4j 统一 R 信封（含 code 字段）").contains("code");
    }

    /**
     * 最小真实 Web 应用（自动装配触发 {@link Ddd4jWebMvcAutoConfiguration} 全链）。
     */
    @SpringBootConfiguration
    @EnableAutoConfiguration
    @Import(ItWebController.class)
    static class ItApp {
    }

    /**
     * IT 控制器。
     */
    @RestController
    static class ItWebController {

        /**
         * 正常端点。
         *
         * @return 固定业务结果
         */
        @GetMapping("/it/ok")
        public String ok() {
            return "it-ok";
        }

        /**
         * 业务异常端点（交由真实统一异常翻译处理）。
         */
        @GetMapping("/it/boom")
        public String boom() {
            throw new IllegalArgumentException("it-boom");
        }
    }
}
