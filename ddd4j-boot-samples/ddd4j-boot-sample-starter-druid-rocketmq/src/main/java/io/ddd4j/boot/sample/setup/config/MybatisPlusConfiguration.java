/**
 * Copyright (C) 2018 ddd4j (http://ddd4j.io).
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
 * MyBatis-Plus 装配配置：开启 Mapper/DAO/Repository 扫描，注册乐观锁、分页、全表防护等插件与 SQL 注入器。
 *
 * @author ddd4j
 * @since 4.0.x
 */
@Configuration
@MapperScan({"io.ddd4j.**.dao,io.ddd4j.**.mapper", "io.ddd4j.**.repository"})
public class MybatisPlusConfiguration {

    /**
     * 无参构造，保持 Spring Bean 默认实例化语义。
     */
    public MybatisPlusConfiguration() {
    }

    /**
     * 乐观锁插件
     *
     * @return 乐观锁内层拦截器
     */
    @Bean
    public OptimisticLockerInnerInterceptor optimisticLockerInterceptor() {
        return new OptimisticLockerInnerInterceptor();
    }

    /**
     * 新的分页插件,一缓和二缓遵循mybatis的规则,需要设置 MybatisConfiguration#useDeprecatedExecutor =
     * false 避免缓存出现问题(该属性会在旧插件移除后一同移除)
     *
     * @return 聚合了全表防护与分页能力的 MyBatis-Plus 拦截器
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
     * @return 分页内层拦截器，查询溢出时不追加页码
     */
    @Bean
    public PaginationInnerInterceptor paginationInterceptor() {
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor();
        paginationInterceptor.setOverflow(false);
        return paginationInterceptor;
    }

    /**
     * 全表更新/删除防护插件，拦截危险的全表 SQL。
     *
     * @return 全表防护内层拦截器
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
