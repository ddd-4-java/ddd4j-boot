/**
 * Copyright (C) 2018 ddd4j (https://github.com/ddd-4-java/ddd4j).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.setup.config;

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
    /**
     * 构造 DataSourceConfig 实例。
     *
     */
    public DataSourceConfig() {
    }

    //注意: @Qualifier 按名称在IOC容器中找指定名称的bean，
    /**
     * 执行 platformTransactionManager 操作。
     *
     * @param myDataSource 目标数据源
     * @return 事务管理器
     */
    @Bean //或者 @Bean("myTransactionManager")
    public PlatformTransactionManager platformTransactionManager(
            @Qualifier("dataSource") DataSource myDataSource) {
        return new DataSourceTransactionManager(myDataSource);
    }

}
