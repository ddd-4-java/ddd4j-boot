/**
 * Copyright (C) 2018 ddd4j (http://ddd4j.io).
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
     * 构造数据源配置类（显式无参构造器，与编译器生成的默认构造器等价）。
     */
    public DataSourceConfig() {
    }

    /**
     * 声明基于数据源的平台事务管理器。
     * <p>
     * 通过 {@link Qualifier} 按名称在容器中查找数据源 Bean，再据此构造事务管理器。
     * </p>
     *
     * @param myDataSource 名称为 {@code dataSource} 的数据源
     * @return 绑定该数据源的事务管理器
     */
    //注意: @Qualifier 按名称在IOC容器中找指定名称的bean，
    @Bean //或者 @Bean("myTransactionManager")
    public PlatformTransactionManager platformTransactionManager(
            @Qualifier("dataSource") DataSource myDataSource) {
        return new DataSourceTransactionManager(myDataSource);
    }

}
