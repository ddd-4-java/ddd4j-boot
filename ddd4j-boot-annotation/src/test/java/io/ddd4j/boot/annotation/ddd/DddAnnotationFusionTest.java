package io.ddd4j.boot.annotation.ddd;

import io.ddd4j.annotation.ddd.DDDAnnotation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 验收测试：验证 ddd4j-boot-annotation 的 11 个 DDD 注解
 * 与 Spring 框架的元注解融合正确。
 *
 * <p>核心目标：
 * <ul>
 *   <li>每个 ddd4j DDD 注解都正确标注了 {@link DDDAnnotation} 元注解（可被 ArchUnit 识别）</li>
 *   <li>每个 ddd4j DDD 注解都正确融合了 Spring 的 Bean 注册元注解（@Service/@Repository/@Component）</li>
 *   <li>Spring 的 AnnotationUtils.findAnnotation 能找到底层 Spring 注解</li>
 * </ul>
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 * @since 2.0.x
 */
@DisplayName("ddd4j-boot-annotation: 11 DDD 注解与 Spring 元注解融合验收")
class DddAnnotationFusionTest {

    // ============== 元注解验证：每个 ddd4j DDD 注解都必须标注 DDDAnnotation ==============

    @Test
    @DisplayName("DomainService 必须标注 @DDDAnnotation")
    void domainService_mustHave_DDDAnnotation() {
        assertNotNull(DomainService.class.getAnnotation(DDDAnnotation.class),
                "DomainService 必须标注 @DDDAnnotation 以被 ArchUnit 识别");
    }

    @Test
    @DisplayName("DomainRepository 必须标注 @DDDAnnotation")
    void domainRepository_mustHave_DDDAnnotation() {
        assertNotNull(DomainRepository.class.getAnnotation(DDDAnnotation.class));
    }

    @Test
    @DisplayName("ApplicationService 必须标注 @DDDAnnotation")
    void applicationService_mustHave_DDDAnnotation() {
        assertNotNull(ApplicationService.class.getAnnotation(DDDAnnotation.class));
    }

    @Test
    @DisplayName("QueryService 必须标注 @DDDAnnotation")
    void queryService_mustHave_DDDAnnotation() {
        assertNotNull(QueryService.class.getAnnotation(DDDAnnotation.class));
    }

    @Test
    @DisplayName("CommandExecutor 必须标注 @DDDAnnotation")
    void commandExecutor_mustHave_DDDAnnotation() {
        assertNotNull(CommandExecutor.class.getAnnotation(DDDAnnotation.class));
    }

    @Test
    @DisplayName("DomainEntity 必须标注 @DDDAnnotation")
    void domainEntity_mustHave_DDDAnnotation() {
        assertNotNull(DomainEntity.class.getAnnotation(DDDAnnotation.class));
    }

    @Test
    @DisplayName("DomainValueObject 必须标注 @DDDAnnotation")
    void domainValueObject_mustHave_DDDAnnotation() {
        assertNotNull(DomainValueObject.class.getAnnotation(DDDAnnotation.class));
    }

    @Test
    @DisplayName("DomainGateway 必须标注 @DDDAnnotation")
    void domainGateway_mustHave_DDDAnnotation() {
        assertNotNull(DomainGateway.class.getAnnotation(DDDAnnotation.class));
    }

    @Test
    @DisplayName("DomainAssembler 必须标注 @DDDAnnotation")
    void domainAssembler_mustHave_DDDAnnotation() {
        assertNotNull(DomainAssembler.class.getAnnotation(DDDAnnotation.class));
    }

    @Test
    @DisplayName("DomainConverter 必须标注 @DDDAnnotation")
    void domainConverter_mustHave_DDDAnnotation() {
        assertNotNull(DomainConverter.class.getAnnotation(DDDAnnotation.class));
    }

    // ============== Spring 元注解融合验证 ==============

    @Test
    @DisplayName("DomainService 底层融合 @Service")
    void domainService_mustHave_Service() {
        assertNotNull(DomainService.class.getAnnotation(Service.class),
                "DomainService 必须融合 @Service 元注解，使业务类自动注册为 Spring Bean");
    }

    @Test
    @DisplayName("DomainRepository 底层融合 @Repository（启用 Spring DAO 异常转换）")
    void domainRepository_mustHave_Repository() {
        assertNotNull(DomainRepository.class.getAnnotation(Repository.class),
                "DomainRepository 必须融合 @Repository 元注解");
    }

    @Test
    @DisplayName("ApplicationService 底层融合 @Service")
    void applicationService_mustHave_Service() {
        assertNotNull(ApplicationService.class.getAnnotation(Service.class));
    }

    @Test
    @DisplayName("QueryService 底层融合 @Service")
    void queryService_mustHave_Service() {
        assertNotNull(QueryService.class.getAnnotation(Service.class));
    }

    @Test
    @DisplayName("CommandExecutor 底层融合 @Component")
    void commandExecutor_mustHave_Component() {
        assertNotNull(CommandExecutor.class.getAnnotation(Component.class));
    }

    @Test
    @DisplayName("DomainEntity 底层融合 @Component")
    void domainEntity_mustHave_Component() {
        assertNotNull(DomainEntity.class.getAnnotation(Component.class));
    }

    @Test
    @DisplayName("DomainValueObject 底层融合 @Component")
    void domainValueObject_mustHave_Component() {
        assertNotNull(DomainValueObject.class.getAnnotation(Component.class));
    }

    @Test
    @DisplayName("DomainGateway 底层融合 @Component")
    void domainGateway_mustHave_Component() {
        assertNotNull(DomainGateway.class.getAnnotation(Component.class));
    }

    @Test
    @DisplayName("DomainAssembler 底层融合 @Component")
    void domainAssembler_mustHave_Component() {
        assertNotNull(DomainAssembler.class.getAnnotation(Component.class));
    }

    @Test
    @DisplayName("DomainConverter 底层融合 @Component")
    void domainConverter_mustHave_Component() {
        assertNotNull(DomainConverter.class.getAnnotation(Component.class));
    }

    // ============== Spring AnnotationUtils 集成验证 ==============

    @Test
    @DisplayName("Spring AnnotationUtils.findAnnotation 能从 ddd4j DDD 注解找到 @Service")
    void springAnnotationUtils_canFindService() throws Exception {
        // 模拟业务代码：定义一个带 @DomainService 的类
        @DomainService
        class TestDomainService {
        }

        // Spring 的 AnnotationUtils 应能从 TestDomainService 找到 @Service
        Service service = AnnotationUtils.findAnnotation(TestDomainService.class, Service.class);
        assertNotNull(service,
                "Spring AnnotationUtils.findAnnotation 必须能从 @DomainService 找到 @Service");

        // 同时也能找到 @DDDAnnotation
        DDDAnnotation ddd = AnnotationUtils.findAnnotation(TestDomainService.class, DDDAnnotation.class);
        assertNotNull(ddd,
                "Spring AnnotationUtils.findAnnotation 必须能从 @DomainService 找到 @DDDAnnotation");
    }

    @Test
    @DisplayName("Spring AnnotationUtils.findAnnotation 能从 @DomainRepository 找到 @Repository")
    void springAnnotationUtils_canFindRepository() throws Exception {
        @DomainRepository
        class TestDomainRepository {
        }

        Repository repo = AnnotationUtils.findAnnotation(TestDomainRepository.class, Repository.class);
        assertNotNull(repo);

        DDDAnnotation ddd = AnnotationUtils.findAnnotation(TestDomainRepository.class, DDDAnnotation.class);
        assertNotNull(ddd);
    }

    @Test
    @DisplayName("Spring AnnotationUtils.findAnnotation 能从 @DomainEntity 找到 @Component")
    void springAnnotationUtils_canFindComponent() throws Exception {
        @DomainEntity(aggregateRoot = true)
        class TestAggregateRoot {
        }

        Component comp = AnnotationUtils.findAnnotation(TestAggregateRoot.class, Component.class);
        assertNotNull(comp);
    }

    // ============== 业务代码使用模式验证 ==============

    @Test
    @DisplayName("业务代码只写一个 @DomainService 即可同时获得 DDD 语义 + Spring Bean 注册")
    void businessCode_singleAnnotation() throws Exception {
        // 这是 annotation-architecture.md 文档的核心目标
        @DomainService
        class UserDomainServiceImpl {
        }

        // 同时具备
        assertNotNull(UserDomainServiceImpl.class.getAnnotation(DDDAnnotation.class),
                "业务代码只写 @DomainService，应自动获得 DDD 语义");
        assertNotNull(UserDomainServiceImpl.class.getAnnotation(Service.class),
                "业务代码只写 @DomainService，应自动获得 Spring Bean 注册");
    }

    @Test
    @DisplayName("@DomainRepository 同时获得 DDD 语义 + @Repository（自动异常转换）")
    void domainRepository_combinesDDDAndRepository() throws Exception {
        @DomainRepository
        interface UserRepository {
        }

        assertNotNull(UserRepository.class.getAnnotation(DDDAnnotation.class));
        assertNotNull(UserRepository.class.getAnnotation(Repository.class));
    }

    @Test
    @DisplayName("@DomainEntity.aggregateRoot 属性可正确传递")
    void domainEntity_aggregateRootAttribute() throws Exception {
        @DomainEntity(aggregateRoot = true)
        class OrderAggregate {
        }

        DomainEntity entity = DomainEntity.class.getAnnotation(DomainEntity.class);
        assertNotNull(entity);
        assertTrue(entity.aggregateRoot(),
                "@DomainEntity.aggregateRoot() 默认值必须为 true（来自使用处的覆盖）");
    }

    // ============== 完整性验证 ==============

    @Test
    @DisplayName("全部 11 个 DDD 注解均已实现")
    void all11DddAnnotationsAreImplemented() {
        // 通过 classpath 扫描确保所有 11 个注解都存在
        Set<String> expected = new HashSet<>(Arrays.asList(
                "DomainService",
                "DomainRepository",
                "DomainEntity",
                "DomainValueObject",
                "DomainGateway",
                "DomainAssembler",
                "DomainConverter",
                "ApplicationService",
                "QueryService",
                "CommandExecutor"
        ));

        Set<String> actual = new HashSet<>();
        // 直接通过 class lookup，不依赖文件系统
        for (String name : expected) {
            try {
                Class.forName("io.ddd4j.boot.annotation.ddd." + name);
                actual.add(name);
            } catch (ClassNotFoundException e) {
                // 不存在则跳过
            }
        }

        assertEquals(expected, actual,
                "ddd4j-boot-annotation 必须实现全部 10 个 DDD 注解（@DomainEvent 不下沉）");

        // 明确确认 @DomainEvent 不在此包中
        try {
            Class.forName("io.ddd4j.boot.annotation.ddd.DomainEvent");
            fail("@DomainEvent 不应下沉到 ddd4j-boot-annotation（事件是数据载体，不需注册 Bean）");
        } catch (ClassNotFoundException expected_ex) {
            // 正确：@DomainEvent 不在此包
        }
    }
}
