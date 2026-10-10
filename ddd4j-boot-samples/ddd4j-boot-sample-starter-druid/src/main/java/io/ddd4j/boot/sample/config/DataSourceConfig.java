/**
 * Copyright (C) 2018 ddd4j (https://github.com/ddd-4-java/ddd4j).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * 开启事务管理,确保在启动类中@component扫描到该类
 */
@Configuration
@EnableTransactionManagement
public class DataSourceConfig {

    //注意: @Qualifier 按名称在IOC容器中找指定名称的bean，

    /**
     * 构造数据源事务配置类实例。
     *
     */
    public DataSourceConfig() {
    }

    /**
     * 注册数据源事务管理器。
     *
     * <p>通过 {@code @Qualifier} 按名称在 IOC 容器中查找名为 dataSource 的数据源，
     * 据此构建 {@link DataSourceTransactionManager}。
     *
     * @param myDataSource 名称为 dataSource 的数据源 Bean
     * @return 数据源事务管理器
     */
    @Bean //或者 @Bean("myTransactionManager")
    public PlatformTransactionManager platformTransactionManager(
            @Qualifier("dataSource") DataSource myDataSource) {
        return new DataSourceTransactionManager(myDataSource);
    }

}
