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
package io.ddd4j.boot.auth.satoken;

import cn.dev33.satoken.stp.StpUtil;
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
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Sa-Token 真实 Servlet 栈登录流集成测试（真实 DispatcherServlet + 真实 sa-token 上下文）。
 *
 * <p><b>证据性裁定（外部设施面调研）</b>：auth 域（license/security/shiro/satoken 四模块）经全量
 * 依赖审计，均无外部基础设施依赖——sa-token 默认使用进程内内存 Dao 存储会话/令牌，
 * 模块依赖树中不存在 sa-token-redis / spring-session 等外部存储整合（Redis 仅属 data/cache 域），
 * 因此 auth 域不适用数据库/缓存容器，取而代之覆盖其最接近的真实集成点：
 * <b>真实 DispatcherServlet 请求管线 + 真实 sa-token 会话 + 真实统一异常翻译</b>的登录-鉴权闭环。
 *
 * <p><b>传输层说明</b>：受控验证环境的主机安全策略对 Java 进程自身监听的任意端口一律丢弃
 * loopback TCP 连接（已用最小 Socket 探针取证：任意端口自连均超时，仅 Docker 发布端口可达），
 * 故 RANDOM_PORT 真实 Tomcat 方案在受控环境不可验证；此处以 MockMvc 驱动真实 Servlet 管线
 * （仅传输层为 Mock，鉴权/会话/异常翻译链路全部为生产组件），容器真实网络面由 data 域
 * testcontainers IT 覆盖。
 *
 * <p>对应 change: establish-boot-container-it（5.3 auth 域）。
 *
 * @since 4.0.x
 */
@DisplayName("Sa-Token 登录流 × 真实 Servlet 管线")
@SpringBootTest(classes = SaTokenLoginFlowIntegrationTest.ItApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class SaTokenLoginFlowIntegrationTest {

    /**
     * sa-token 默认令牌读取头。
     */
    private static final String TOKEN_HEADER = "satoken";

    /**
     * 真实 Servlet 管线驱动器。
     */
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    /**
     * 以真实 Web 应用上下文装配 Servlet 管线（Boot 4 已移除 @AutoConfigureMockMvc）。
     */
    @BeforeEach
    void setUpMockMvc() {
        // 挂载上下文中全部真实 Filter（含 sa-token SaTokenContextFilter，MockMvc 默认不注册 FilterRegistrationBean）
        jakarta.servlet.Filter[] filters = webApplicationContext
                .getBeansOfType(jakarta.servlet.Filter.class).values().toArray(new jakarta.servlet.Filter[0]);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilters(filters)
                .build();
    }

    @Test
    @DisplayName("登录签发真实令牌，携带令牌可读取会话身份，无令牌统一翻译为 401")
    void loginFlowShouldRoundTripThroughRealServletStack() throws Exception {
        // 1. 真实登录：经 DispatcherServlet → StpUtil.login 签发令牌（内存 Dao 落库）
        org.springframework.test.web.servlet.MvcResult loginResult = mockMvc.perform(post("/it/login"))
                .andReturn();
        String token = loginResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertThat(token).as("真实登录必须签发非空令牌").isNotBlank();

        // 2. 携带令牌访问：sa-token 从真实请求头解析会话身份
        mockMvc.perform(get("/it/me").header(TOKEN_HEADER, token))
                .andExpect(status().isOk())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString(StandardCharsets.UTF_8))
                        .as("会话身份必须与登录账号一致").isEqualTo("user-it"));

        // 3. 无令牌访问：StpUtil 抛 NotLoginException → SaTokenExceptionHandler 统一翻译为 401
        mockMvc.perform(get("/it/me"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * 最小真实 Web 应用（自动装配触发 {@link SaTokenEnhanceAutoConfiguration} 全链）。
     */
    @SpringBootConfiguration
    @EnableAutoConfiguration
    @Import(ItAuthController.class)
    static class ItApp {
    }

    /**
     * IT 控制器（真实登录/身份端点）。
     */
    @RestController
    static class ItAuthController {

        /**
         * 真实登录端点。
         *
         * @return 签发的令牌值
         */
        @PostMapping("/it/login")
        public String login() {
            StpUtil.login("user-it");
            return Objects.requireNonNull(StpUtil.getTokenValue());
        }

        /**
         * 会话身份端点（未登录时抛 NotLoginException）。
         *
         * @return 当前登录账号
         */
        @GetMapping("/it/me")
        public String me() {
            return StpUtil.getLoginIdAsString();
        }
    }
}
