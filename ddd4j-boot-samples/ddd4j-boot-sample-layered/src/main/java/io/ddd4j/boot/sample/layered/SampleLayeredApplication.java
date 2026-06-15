package io.ddd4j.boot.sample.layered;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * DDD 分层架构示例启动类。
 *
 * <p>演示 ddd4j-boot 的"CRUD 优先"轨道：
 * <ul>
 *   <li>充血 Model（继承即得 save/update/delete）</li>
 *   <li>充血 Query（继承即得 page/list/one/count/exist + 断言）</li>
 *   <li>四泛型 BaseRepositoryImpl（零样板仓储）</li>
 *   <li>DDD 注解标注分层角色（@DomainEntity/@DomainRepository）</li>
 * </ul>
 *
 * @author wandl
 */
@SpringBootApplication
@MapperScan("io.ddd4j.boot.sample.layered.infrastructure.persistence")
public class SampleLayeredApplication {

    public static void main(String[] args) {
        SpringApplication.run(SampleLayeredApplication.class, args);
    }

}
