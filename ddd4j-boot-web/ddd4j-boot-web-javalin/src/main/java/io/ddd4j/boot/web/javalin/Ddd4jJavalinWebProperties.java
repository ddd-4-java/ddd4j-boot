package io.ddd4j.boot.web.javalin;

import io.ddd4j.web.core.auth.AuthenticationMode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * ddd4j Javalin Web 的 Spring Boot 配置属性。
 *
 * <p>命名空间 {@code ddd4j.web.javalin.*}，字段与默认值对齐上游
 * {@code io.ddd4j.web.helidon.Ddd4jHelidonWebConfiguration} 的配置持有者模式。
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "ddd4j.web.javalin")
public class Ddd4jJavalinWebProperties {

    /** 无需认证即可访问的路径。 */
    private List<String> publicPaths = new ArrayList<>(List.of(
            "/health", "/health/readiness", "/health/liveness"));

    /** 默认认证模式。 */
    private AuthenticationMode defaultAuthenticationMode = AuthenticationMode.REQUIRED;

    /** 是否信任 X-Forwarded-* 请求头解析客户端 IP。 */
    private boolean trustForwardedHeaders;

    /** 是否启用幂等防护。 */
    private boolean idempotencyEnabled = true;

    /** 幂等防护使用的缓存名。 */
    private String idempotencyCacheName = "ddd4j-web-idempotency";

    /** 幂等防护的默认 TTL。 */
    private Duration idempotencyTtl = Duration.ofMinutes(5);
}
