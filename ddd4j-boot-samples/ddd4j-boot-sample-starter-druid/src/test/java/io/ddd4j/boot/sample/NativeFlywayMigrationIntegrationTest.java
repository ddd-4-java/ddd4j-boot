package io.ddd4j.boot.sample;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 验证 Boot 4 样例使用原生 Flyway 完成 MySQL 迁移。
 */
@Testcontainers(disabledWithoutDocker = true)
class NativeFlywayMigrationIntegrationTest {

    @Container
    static final GenericContainer<?> MYSQL = new GenericContainer<>(DockerImageName.parse("mysql:8.4"))
            .withEnv("MYSQL_ROOT_PASSWORD", "ddd4j")
            .withEnv("MYSQL_DATABASE", "ddd4j_boot")
            .withExposedPorts(3306)
            .waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(120)));

    @Test
    void shouldApplyNativeMigrationAndCreateDemoTable() throws Exception {
        String jdbcUrl = "jdbc:mysql://" + MYSQL.getHost() + ":" + MYSQL.getMappedPort(3306)
                + "/ddd4j_boot?useSSL=false&allowPublicKeyRetrieval=true";

        Flyway flyway = Flyway.configure()
                .dataSource(jdbcUrl, "root", "ddd4j")
                .locations("classpath:db/migration")
                .load();

        assertThat(flyway.migrate().migrationsExecuted).isEqualTo(1);
        try (Connection connection = DriverManager.getConnection(jdbcUrl, "root", "ddd4j");
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery("SELECT COUNT(*) FROM t_demo")) {
            assertThat(result.next()).isTrue();
            assertThat(result.getInt(1)).isZero();
        }
    }
}
