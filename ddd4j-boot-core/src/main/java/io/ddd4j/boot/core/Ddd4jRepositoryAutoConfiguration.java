package io.ddd4j.boot.core;

import io.ddd4j.core.cqrs.command.CommandBus;
import io.ddd4j.core.cqrs.command.CommandExecutor;
import io.ddd4j.core.cqrs.command.DefaultCommandBus;
import io.ddd4j.core.ddd.model.AggregateRoot;
import io.ddd4j.core.ddd.repository.Repository;
import io.ddd4j.core.ddd.repository.RepositoryRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ddd4j Repository 与 CQRS 自动配置。
 *
 * <p>自动发现 Spring 容器中的 {@link Repository} Bean 并注册到 {@link RepositoryRegistry}，
 * 同时收集 {@link CommandExecutor} 构建 {@link CommandBus}。
 *
 * @author wandl
 * @since 3.4.x
 */
@AutoConfiguration(after = Ddd4jCoreAutoConfiguration.class)
@ConditionalOnClass({RepositoryRegistry.class, CommandBus.class})
public class Ddd4jRepositoryAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(Ddd4jRepositoryAutoConfiguration.class);

    /**
     * Repository 自动注册器：扫描 Spring 容器中的 Repository Bean 并注册到 RepositoryRegistry。
     */
    @Bean
    @ConditionalOnMissingBean
    public Ddd4jRepositoryRegistrar ddd4jRepositoryRegistrar() {
        return new Ddd4jRepositoryRegistrar();
    }

    /**
     * CommandBus 自动装配：收集所有 CommandExecutor 并构建唯一的 CommandBus。
     */
    @Bean
    @ConditionalOnMissingBean(CommandBus.class)
    public CommandBus commandBus(List<CommandExecutor<?>> commandExecutors) {
        if (commandExecutors.isEmpty()) {
            log.warn("ddd4j CQRS: No CommandExecutor beans found. CommandBus will be empty.");
        }
        return new DefaultCommandBus(commandExecutors);
    }

    /**
     * Repository 自动注册器：BeanPostProcessor 方式扫描 Repository Bean。
     *
     * <p>当 Repository Bean 初始化完成后，自动检测其泛型参数中的 AggregateRoot 类型，
     * 并注册到 {@link RepositoryRegistry}。
     */
    public static class Ddd4jRepositoryRegistrar implements BeanPostProcessor {

        private static final Logger log = LoggerFactory.getLogger(Ddd4jRepositoryRegistrar.class);

        /**
         * 已注册的仓储实例映射（用于测试清理）。
         */
        private final Map<Class<?>, Repository> registeredRepositories = new ConcurrentHashMap<>();

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) {
            if (bean instanceof Repository repository) {
                registerRepository(beanName, repository);
            }
            return bean;
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        private void registerRepository(String beanName, Repository repository) {
            // 尝试从 Repository 接口泛型参数中提取 AggregateRoot 类型
            Class<?> aggregateType = extractAggregateType(repository);
            if (aggregateType != null && AggregateRoot.class.isAssignableFrom(aggregateType)) {
                Class<? extends AggregateRoot> aggClass = (Class<? extends AggregateRoot>) aggregateType;
                // 避免重复注册
                if (!registeredRepositories.containsKey(aggClass)) {
                    RepositoryRegistry.register(aggClass, repository);
                    registeredRepositories.put(aggClass, repository);
                    log.info("ddd4j Repository: Registered {} for aggregate {}", beanName, aggClass.getSimpleName());
                }
            }
        }

        /**
         * 从 Repository 实现类的泛型参数中提取 AggregateRoot 类型。
         */
        private Class<?> extractAggregateType(Repository repository) {
            // 遍历实现的接口，查找 Repository<M, ID> 的泛型参数
            for (java.lang.reflect.Type type : repository.getClass().getGenericInterfaces()) {
                if (type instanceof java.lang.reflect.ParameterizedType pt) {
                    if (pt.getRawType() == Repository.class) {
                        java.lang.reflect.Type[] typeArgs = pt.getActualTypeArguments();
                        if (typeArgs.length > 0 && typeArgs[0] instanceof Class) {
                            return (Class<?>) typeArgs[0];
                        }
                    }
                }
            }
            // 递归查找父类
            Class<?> superClass = repository.getClass().getSuperclass();
            while (superClass != null && superClass != Object.class) {
                for (java.lang.reflect.Type type : superClass.getGenericInterfaces()) {
                    if (type instanceof java.lang.reflect.ParameterizedType pt) {
                        if (pt.getRawType() == Repository.class) {
                            java.lang.reflect.Type[] typeArgs = pt.getActualTypeArguments();
                            if (typeArgs.length > 0 && typeArgs[0] instanceof Class) {
                                return (Class<?>) typeArgs[0];
                            }
                        }
                    }
                }
                superClass = superClass.getSuperclass();
            }
            return null;
        }

        /**
         * 获取已注册的仓储实例映射（用于测试）。
         */
        public Map<Class<?>, Repository> getRegisteredRepositories() {
            return java.util.Collections.unmodifiableMap(registeredRepositories);
        }
    }
}
