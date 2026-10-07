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
package io.ddd4j.boot.data.mybatis;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.spring.MybatisSqlSessionFactoryBean;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.DockerClientFactory;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * {@link Ddd4jMybatisAutoConfiguration} 装配的 {@code MybatisPlusInterceptor} 真实 MySQL 集成测试。
 *
 * <p>用 testcontainers 官方 MySQL 模块拉起真实 MySQL 8，逐项验证模块承诺的三个内嵌拦截器：
 * <ul>
 *   <li>PaginationInnerInterceptor — 真实 LIMIT 分页与 COUNT 总数</li>
 *   <li>OptimisticLockerInnerInterceptor — @Version 条件更新与失败零命中</li>
 *   <li>BlockAttackInnerInterceptor — 拦截无 WHERE 的全表 DELETE</li>
 * </ul>
 *
 * <p>夹具为进程级单例容器（{@link MysqlMybatisContainerSupport}），Docker 不可用整体跳过。
 *
 * <p>对应 change: establish-boot-container-it（5.3 data 域容器测试）。
 *
 * @since 4.0.x
 */
@DisplayName("MybatisPlusInterceptor × 真实 MySQL（testcontainers）")
class MybatisInterceptorsMysqlIntegrationTest {

    /**
     * 共享 SqlSessionFactory（进程内一次性构建，全部用例复用）。
     */
    private static SqlSessionFactory factory;

    /**
     * 共享自动提交会话（用例结束关闭，避免连接池耗尽）。
     */
    private static SqlSession session;

    /**
     * 共享连接池（用例结束关闭）。
     */
    private static HikariDataSource dataSource;

    @BeforeAll
    static void setUpFactory() throws Exception {
        assumeTrue(DockerClientFactory.instance().isDockerAvailable(), "Docker 不可用，跳过真实 MySQL IT");
        dataSource = new HikariDataSource(hikariConfig());
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE t_it_user ("
                    + "id BIGINT AUTO_INCREMENT PRIMARY KEY, "
                    + "name VARCHAR(64) NOT NULL, "
                    + "age INT NOT NULL, "
                    + "version INT NOT NULL DEFAULT 0) ENGINE=InnoDB");
        }
        // 直接装配模块真实 Bean：Ddd4jMybatisAutoConfiguration#mybatisPlusInterceptor
        MybatisPlusInterceptorHolder holder = new MybatisPlusInterceptorHolder();
        MybatisSqlSessionFactoryBean bean = new MybatisSqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setMapUnderscoreToCamelCase(true);
        bean.setConfiguration(configuration);
        bean.setPlugins(holder.interceptor());
        factory = bean.getObject();
        // 工厂构建后注册 MP BaseMapper（触发 DefaultSqlInjector 注入 CRUD 方法）
        configuration.addMapper(ItUserMapper.class);
        session = factory.openSession(true);
    }

    @AfterAll
    static void closeSessionAndDataSource() {
        if (Objects.nonNull(session)) {
            session.close();
        }
        if (Objects.nonNull(dataSource)) {
            dataSource.close();
        }
    }

    @Test
    @DisplayName("分页拦截器：真实 LIMIT 分页与 COUNT 总数")
    void paginationInterceptorShouldPageAgainstRealMysql() {
        ItUserMapper mapper = mapper();
        long stamp = System.nanoTime();
        for (int i = 0; i < 5; i++) {
            mapper.insert(newUser("page-" + stamp + "-" + i, 20 + i));
        }
        Page<ItUser> page = mapper.selectPage(
                new Page<>(2, 2),
                new LambdaQueryWrapper<ItUser>().like(ItUser::getName, "page-" + stamp).orderByAsc(ItUser::getId));
        assertThat(page.getTotal()).as("COUNT 总数必须来自真实 MySQL").isEqualTo(5L);
        assertThat(page.getRecords()).as("第 2 页每页 2 条（LIMIT 注入生效）").hasSize(2);
        assertThat(page.getRecords().get(0).getAge()).isEqualTo(22);
    }

    @Test
    @DisplayName("乐观锁拦截器：@Version 条件更新生效，旧版本更新零命中")
    void optimisticLockerShouldGuardVersionAgainstRealMysql() {
        ItUserMapper mapper = mapper();
        ItUser user = newUser("lock-" + System.nanoTime(), 30);
        mapper.insert(user);
        ItUser loaded = mapper.selectById(user.getId());
        assertThat(loaded.getVersion()).isEqualTo(0);
        loaded.setAge(31);
        assertThat(mapper.updateById(loaded)).as("版本匹配时更新成功").isEqualTo(1);
        assertThat(mapper.selectById(user.getId()).getVersion()).as("更新后版本自增").isEqualTo(1);
        // 成功更新后 MP 已把实体 version 回写为 1；显式回拨为 0 模拟过期副本：WHERE version=0 不命中
        loaded.setVersion(0);
        loaded.setAge(99);
        assertThat(mapper.updateById(loaded)).as("旧版本更新必须零命中").isZero();
        assertThat(mapper.selectById(user.getId()).getAge()).as("数据库值不被旧版本覆盖").isEqualTo(31);
    }

    @Test
    @DisplayName("防全表攻击拦截器：无 WHERE 的 DELETE 被拒绝")
    void blockAttackShouldRejectFullTableDelete() {
        ItUserMapper mapper = mapper();
        assertThatThrownBy(() -> mapper.delete(new LambdaQueryWrapper<>()))
                .as("无 WHERE 的全表 DELETE 必须被 BlockAttackInnerInterceptor 拦截")
                .hasRootCauseInstanceOf(MybatisPlusException.class)
                .hasRootCauseMessage("Prohibition of full table deletion");
    }

    /**
     * 构建真实 MySQL 连接池配置。
     *
     * @return Hikari 配置
     */
    private static HikariConfig hikariConfig() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(MysqlMybatisContainerSupport.MYSQL.getJdbcUrl());
        config.setUsername(MysqlMybatisContainerSupport.MYSQL.getUsername());
        config.setPassword(MysqlMybatisContainerSupport.MYSQL.getPassword());
        config.setMaximumPoolSize(2);
        config.setPoolName("it-mybatis-mysql");
        return config;
    }

    /**
     * 取当前 mapper 代理。
     *
     * @return 用户 mapper
     */
    private static ItUserMapper mapper() {
        return session.getMapper(ItUserMapper.class);
    }

    /**
     * 构建测试用户。
     *
     * @param name 用户名
     * @param age  年龄
     * @return 实体
     */
    private static ItUser newUser(final String name, final int age) {
        ItUser user = new ItUser();
        user.setName(name);
        user.setAge(age);
        return user;
    }

    /**
     * IT 用户实体（@Version 乐观锁列）。
     */
    @TableName("t_it_user")
    static class ItUser {

        /**
         * 自增主键。
         */
        @TableId(type = IdType.AUTO)
        private Long id;

        /**
         * 用户名。
         */
        private String name;

        /**
         * 年龄。
         */
        private Integer age;

        /**
         * 乐观锁版本号。
         */
        @Version
        private Integer version;

        Long getId() {
            return id;
        }

        String getName() {
            return name;
        }

        void setName(final String name) {
            this.name = name;
        }

        Integer getAge() {
            return age;
        }

        void setAge(final Integer age) {
            this.age = age;
        }

        Integer getVersion() {
            return version;
        }

        void setVersion(final Integer version) {
            this.version = version;
        }
    }

    /**
     * IT 用户 mapper（BaseMapper 全功能注入）。
     */
    interface ItUserMapper extends BaseMapper<ItUser> {
    }

    /**
     * 模块真实 Bean 的轻量持用（避免测试直接依赖装配类非公共构造细节）。
     */
    private static final class MybatisPlusInterceptorHolder {

        /**
         * 产出与 {@link Ddd4jMybatisAutoConfiguration} 相同配置的拦截器。
         *
         * @return MP 拦截器
         */
        MybatisPlusInterceptor interceptor() {
            return new Ddd4jMybatisAutoConfiguration().mybatisPlusInterceptor();
        }
    }
}
