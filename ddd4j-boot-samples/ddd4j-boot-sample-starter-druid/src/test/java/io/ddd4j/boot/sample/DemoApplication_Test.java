/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest(
        classes = DemoApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.datasource.url=jdbc:h2:mem:ddd4j_sample;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.datasource.druid.routable=false",
                "spring.datasource.druid.url=jdbc:h2:mem:ddd4j_sample;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
                "spring.datasource.druid.username=sa",
                "spring.datasource.druid.password=",
                "spring.datasource.druid.driver-class-name=org.h2.Driver",
                "spring.datasource.druid.validation-query=SELECT 1",
                "spring.flyway.locations=classpath:db/migration/h2",
                "spring.redis.host=127.0.0.1",
                "spring.redis.port=1"
        })
public class DemoApplication_Test {


    /**
     * @LocalServerPort 提供了 @Value("${local.server.port}") 的代替
     */
    @LocalServerPort
    private int port;
    private URL base;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() throws Exception {
        String url = String.format("http://localhost:%d/", port);
        System.out.println(String.format("port is : [%d]", port));
        this.base = new URL(url);
    }

    /**
     * 向"/test"地址发送请求，并打印返回结果
     *
     * @throws Exception
     */
    @Test
    public void createsDemoThroughHttpAndPersistsWithDruid() {

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", "sample-name");
        requestBody.put("intro", "sample-description");
        requestBody.put("orderBy", 1);
        requestBody.put("status", 1);

        ResponseEntity<String> response = this.restTemplate.postForEntity(
                this.base.toString() + "/api/demos", requestBody, String.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).contains("sample-name");
        Integer count = jdbcTemplate.queryForObject(
                "select count(*) from t_demo where name = ?", Integer.class, "sample-name");
        Assertions.assertThat(count).isEqualTo(1);
    }

}
