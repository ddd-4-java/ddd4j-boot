package io.ddd4j.boot.sample.auth.satoken;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ddd4j-boot-auth + sa-token 示例启动类。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@SpringBootApplication
public class SaTokenAuthApplication {

    /**
     * 构造 SaTokenAuthApplication 实例。
     *
     */
    public SaTokenAuthApplication() {
    }

    /**
     * sa-token 鉴权示例应用的启动类，负责引导 Spring 容器启动。
     *
     * @param args 命令行参数，由 Spring 启动流程使用
     */
    public static void main(String[] args) {
        SpringApplication.run(SaTokenAuthApplication.class, args);
    }

}
