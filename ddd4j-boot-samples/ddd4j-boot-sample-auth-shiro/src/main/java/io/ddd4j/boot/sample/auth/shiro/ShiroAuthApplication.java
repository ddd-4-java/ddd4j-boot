package io.ddd4j.boot.sample.auth.shiro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ddd4j-boot-auth + Shiro 示例启动类。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@SpringBootApplication
public class ShiroAuthApplication {

    /**
     * 构造 ShiroAuthApplication 实例。
     *
     */
    public ShiroAuthApplication() {
    }

    /**
     * Shiro 鉴权示例应用的启动类，负责引导 Spring 容器启动。
     *
     * @param args 命令行参数，由 Spring 启动流程使用
     */
    public static void main(String[] args) {
        SpringApplication.run(ShiroAuthApplication.class, args);
    }

}
