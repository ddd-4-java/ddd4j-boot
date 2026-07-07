package io.ddd4j.boot.data.logs;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import io.ddd4j.data.logs.aspect.ApiOperationLogAspect;
import io.ddd4j.data.logs.aspect.ApiOperationLogProvider;
import io.ddd4j.data.logs.aspect.DefaultApiOperationLogProvider;
import io.ddd4j.kit.lang.IdKit;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ddd4j API 操作日志 Spring Boot 自动配置。
 *
 * <p>从 {@code ddd4j-data-logs} 迁入 boot 层。
 * 通用层仅保留纯 AspectJ 的切面实现，由本类负责：
 * <ul>
 *   <li>装配默认 {@link ApiOperationLogProvider}</li>
 *   <li>装配 {@link Snowflake}（若上下文未提供）</li>
 *   <li>装配 {@link ApiOperationLogAspect}（构造方法注入 Snowflake + Provider）</li>
 * </ul>
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "ddd4j.logs", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnClass(ApiOperationLogAspect.class)
public class Ddd4jApiLogAspectAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(ApiOperationLogProvider.class)
    public ApiOperationLogProvider apiOperationLogProvider() {
        return new DefaultApiOperationLogProvider();
    }

    /**
     * 装配雪花算法 ID 生成器。
     * <p>仅当上下文未提供 {@link Snowflake} Bean 时生效（例如 {@code ddd4j-boot-data-external}
     * 已注册 {@code snowflakeIdGenerator}，则此处不会重复装配）。</p>
     *
     * @return Snowflake 实例
     */
    @Bean
    @ConditionalOnMissingBean(Snowflake.class)
    public Snowflake apiOperationLogSnowflake() {
        long datacenterId = IdUtil.getDataCenterId(31);
        long workerId = IdUtil.getWorkerId(datacenterId, 31);
        return IdKit.getSnowflake(workerId, datacenterId);
    }

    /**
     * 装配 API 操作日志切面。
     *
     * @param snowflake    雪花算法 ID 生成器
     * @param logProvider  操作日志提供者
     * @return 切面实例
     */
    @Bean
    public ApiOperationLogAspect apiOperationLogAspect(Snowflake snowflake, ApiOperationLogProvider logProvider) {
        return new ApiOperationLogAspect(snowflake, logProvider);
    }

}
