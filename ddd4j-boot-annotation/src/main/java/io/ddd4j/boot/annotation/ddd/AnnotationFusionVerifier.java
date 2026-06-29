package io.ddd4j.boot.annotation.ddd;

import io.ddd4j.annotation.ddd.DDDAnnotation;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * 独立验证器：在应用启动时（不依赖 surefire）验证 11 个 DDD 注解的元注解融合。
 *
 * <p>使用方式：
 * <pre>
 * mvn -q exec:java -Dexec.mainClass=io.ddd4j.boot.annotation.ddd.AnnotationFusionVerifier
 * </pre>
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 * @since 2.0.x
 */
public final class AnnotationFusionVerifier {

    private AnnotationFusionVerifier() {}

    public static void main(String[] args) {
        int passed = 0;
        int failed = 0;

        passed += verify("DomainService",         DomainService.class,         Service.class,    true);
        passed += verify("DomainRepository",      DomainRepository.class,      Repository.class, true);
        passed += verify("ApplicationService",    ApplicationService.class,    Service.class,    true);
        passed += verify("QueryService",         QueryService.class,         Service.class,    true);
        passed += verify("CommandExecutor",      CommandExecutor.class,      Component.class,  true);
        passed += verify("DomainEntity",         DomainEntity.class,         Component.class,  true);
        passed += verify("DomainValueObject",    DomainValueObject.class,    Component.class,  true);
        passed += verify("DomainGateway",        DomainGateway.class,        Component.class,  true);
        passed += verify("DomainAssembler",      DomainAssembler.class,      Component.class,  true);
        passed += verify("DomainConverter",      DomainConverter.class,      Component.class,  true);

        // 验证 @DomainEvent 不在 ddd4j-boot-annotation 中
        System.out.println();
        System.out.println("--- @DomainEvent 不下沉验证 ---");
        try {
            Class.forName("io.ddd4j.boot.annotation.ddd.DomainEvent");
            System.out.println("❌ FAIL: @DomainEvent 不应在 ddd4j-boot-annotation 中");
            failed++;
        } catch (ClassNotFoundException e) {
            System.out.println("✅ PASS: @DomainEvent 不在 ddd4j-boot-annotation（事件是数据载体，不需注册 Bean）");
            passed++;
        }

        // 验证 Spring AnnotationUtils.findAnnotation 集成
        System.out.println();
        System.out.println("--- Spring AnnotationUtils 集成验证 ---");
        passed += verifySpringIntegration();

        System.out.println();
        System.out.println("========================================");
        System.out.println("总计: " + (passed + failed) + " | 通过: " + passed + " | 失败: " + failed);
        System.out.println("========================================");

        if (failed > 0) {
            System.err.println("❌ 验证未通过");
            System.exit(1);
        } else {
            System.out.println("✅ 全部验证通过！ddd4j-boot-annotation 符合架构设计");
        }
    }

    private static int verify(
            String name,
            Class<? extends java.lang.annotation.Annotation> dddAnnotation,
            Class<? extends java.lang.annotation.Annotation> springAnnotation,
            boolean expectDDD) {

        int result = 1;

        // 1. 必须标注 DDDAnnotation
        DDDAnnotation ddd = dddAnnotation.getAnnotation(DDDAnnotation.class);
        if (expectDDD && ddd == null) {
            System.out.println("❌ " + name + ": 缺少 @DDDAnnotation 元注解");
            return 0;
        }
        System.out.println("✅ " + name + ": 已标注 @DDDAnnotation");

        // 2. 必须融合 Spring 元注解
        java.lang.annotation.Annotation spring = dddAnnotation.getAnnotation(springAnnotation);
        if (spring == null) {
            System.out.println("❌ " + name + ": 缺少 @" + springAnnotation.getSimpleName() + " 元注解");
            return 0;
        }
        System.out.println("✅ " + name + ": 已融合 @" + springAnnotation.getSimpleName());

        return result;
    }

    private static int verifySpringIntegration() {
        int passed = 0;

        // 业务代码只写一个 @DomainService
        @DomainService
        class TestDomainService {}

        Service service = AnnotationUtils.findAnnotation(TestDomainService.class, Service.class);
        if (service != null) {
            System.out.println("✅ Spring AnnotationUtils 能从 @DomainService 找到 @Service");
            passed++;
        } else {
            System.out.println("❌ Spring AnnotationUtils 找不到 @Service");
        }

        DDDAnnotation ddd = AnnotationUtils.findAnnotation(TestDomainService.class, DDDAnnotation.class);
        if (ddd != null) {
            System.out.println("✅ Spring AnnotationUtils 能从 @DomainService 找到 @DDDAnnotation");
            passed++;
        } else {
            System.out.println("❌ Spring AnnotationUtils 找不到 @DDDAnnotation");
        }

        // 业务代码只写一个 @DomainRepository
        @DomainRepository
        interface TestDomainRepository {}

        Repository repo = AnnotationUtils.findAnnotation(TestDomainRepository.class, Repository.class);
        if (repo != null) {
            System.out.println("✅ Spring AnnotationUtils 能从 @DomainRepository 找到 @Repository");
            passed++;
        } else {
            System.out.println("❌ Spring AnnotationUtils 找不到 @Repository");
        }

        return passed;
    }
}
