/**
 * Copyright (C) 2018 ddd4j (https://github.com/ddd-4-java/ddd4j).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.setup.config;

import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 核心插件装配配置。
 * <p>
 * 通过 {@code @MapperScan} 扫描持久层接口所在包，并声明乐观锁、分页、防全表更新删除等内置插件，
 * 统一数据访问层的通用能力。
 * </p>
 */
@Configuration
@MapperScan({"io.ddd4j.**.mapper,io.ddd4j.**.mapper", "io.ddd4j.**.repository"})
public class MybatisPlusConfiguration {

    /**
     * 构造 MyBatis-Plus 配置类（显式无参构造器，与编译器生成的默认构造器等价）。
     */
    public MybatisPlusConfiguration() {
    }

    /**
     * 乐观锁插件
     *
     * @return 乐观锁拦截器
     */
    @Bean
    public OptimisticLockerInnerInterceptor optimisticLockerInterceptor() {
        return new OptimisticLockerInnerInterceptor();
    }

    /**
     * 新的分页插件,一缓和二缓遵循mybatis的规则,需要设置 MybatisConfiguration#useDeprecatedExecutor =
     * false 避免缓存出现问题(该属性会在旧插件移除后一同移除)
     *
     * @return 组合了防全表攻击与分页能力的 MyBatis-Plus 拦截器
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(blockAttackInnerInterceptor());
        interceptor.addInnerInterceptor(paginationInterceptor());
        return interceptor;
    }

    /**
     * mybatis-plus分页插件<br>
     * 文档：http://mp.baomidou.com<br>
     *
     * @return 禁止分页总数溢出的分页拦截器
     */
    @Bean
    public PaginationInnerInterceptor paginationInterceptor() {
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor();
        paginationInterceptor.setOverflow(false);
        return paginationInterceptor;
    }

    /**
     * 防止全表更新与删除的攻击拦截器。
     *
     * @return 阻断全表更新/删除 SQL 的拦截器
     */
    @Bean
    public BlockAttackInnerInterceptor blockAttackInnerInterceptor() {
        BlockAttackInnerInterceptor sqlExplainInterceptor = new BlockAttackInnerInterceptor();
        return sqlExplainInterceptor;
    }

    /**
     * 注入sql注入器
     *
     * @return 默认 SQL 注入器
     */
    @Bean
    public ISqlInjector sqlInjector() {
        return new DefaultSqlInjector();
    }

    /**
     * 注入主键生成器
     @Bean public IKeyGenerator keyGenerator() {
     return new H2KeyGenerator();
     }
     */

    /*
     * oracle数据库配置JdbcTypeForNull
     * 参考：https://gitee.com/baomidou/mybatisplus-boot-starter/issues/IHS8X
     * 不需要这样配置了，参考 yml: mybatis-plus: confuguration dbc-type-for-null: 'null'
     *
     * @Bean public ConfigurationCustomizer configurationCustomizer(){ return new
     * MybatisPlusCustomizers(); }
     *
     * class MybatisPlusCustomizers implements ConfigurationCustomizer {
     *
     * @Override public void customize(org.apache.ibatis.session.Configuration
     * configuration) { configuration.setJdbcTypeForNull(JdbcType.NULL); } }
     */

}
