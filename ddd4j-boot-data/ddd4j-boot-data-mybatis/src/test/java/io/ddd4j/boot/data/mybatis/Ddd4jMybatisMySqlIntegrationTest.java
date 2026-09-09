package io.ddd4j.boot.data.mybatis;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 使用真实 MySQL 验证数据源连接、SQL 往返和 MyBatis-Plus 拦截器装配。
 */
@Testcontainers(disabledWithoutDocker = true)
class Ddd4jMybatisMySqlIntegrationTest {

    @Container
    static final MySQLContainer<?> MYSQL = new MySQLContainer<>(DockerImageName.parse("mysql:8.4"))
            .withDatabaseName("ddd4j")
            .withUsername("ddd4j")
            .withPassword("ddd4j");

    @Test
    void shouldConnectExecuteSqlAndAssembleInterceptor() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(Ddd4jMybatisAutoConfiguration.class))
                .withUserConfiguration(MySqlDataSourceConfiguration.class)
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasSingleBean(MybatisPlusInterceptor.class);
                    JdbcTemplate jdbc = new JdbcTemplate(context.getBean(DataSource.class));
                    jdbc.execute("CREATE TABLE boot_contract (id BIGINT PRIMARY KEY, name VARCHAR(64))");
                    assertThat(jdbc.update("INSERT INTO boot_contract (id, name) VALUES (?, ?)", 1L, "ddd4j"))
                            .isEqualTo(1);
                    assertThat(jdbc.queryForObject(
                            "SELECT name FROM boot_contract WHERE id = ?", String.class, 1L))
                            .isEqualTo("ddd4j");
                });
    }

    @Configuration(proxyBeanMethods = false)
    static class MySqlDataSourceConfiguration {

        @Bean
        DataSource dataSource() {
            return new DriverManagerDataSource(MYSQL.getJdbcUrl(), MYSQL.getUsername(), MYSQL.getPassword());
        }
    }
}
