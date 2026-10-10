package io.ddd4j.boot.cmpt.datascope;

import io.ddd4j.boot.cmpt.datascope.annotation.RequiresDataPermissions;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.extension.utils.SpringContextUtils;

import java.util.Objects;

/**
 * 数据权限校验
 * Data permission verification
 *
 * @author wandl
 * @see RequiresDataPermissions
 */
@Slf4j
public class RequiresDataPermissionsValidator implements ConstraintValidator<RequiresDataPermissions, Object> {

    private String dataType;
    private DataScopeProvider provider;

    /**
     * 显式无参构造器，供 Bean Validation 框架实例化本校验器。
     */
    public RequiresDataPermissionsValidator() {
    }

    /**
     * 初始化校验器：读取注解上的数据类型，并从 Spring 容器获取数据权限提供者。
     *
     * @param annotation {@link RequiresDataPermissions} 注解实例
     */
    @Override
    public void initialize(RequiresDataPermissions annotation) {
        this.dataType = annotation.dataType();
        this.provider = SpringContextUtils.getContext().getApplicationContext().getBean(DataScopeProvider.class);
    }

    /**
     * 校验数据是否具备数据权限：数据或提供者缺失直接判 false，其余委托提供者判定。
     *
     * @param data                   待校验数据（不允许为 {@code null}，否则直接判 false）
     * @param constraintValidatorContext Bean Validation 上下文（本实现未使用）
     * @return 具备权限返回 {@code true}
     */
    @Override
    public boolean isValid(Object data, ConstraintValidatorContext constraintValidatorContext) {
        // Check if the data has value
        if (Objects.isNull(data)) {
            return Boolean.FALSE;
        }
        // Get the data permission provider
        if (Objects.isNull(provider)) {
            log.warn("DataScopeProvider is not found.");
            return Boolean.FALSE;
        }
        // Check if the data has permissions
        return provider.hasPermissions(dataType, data);
    }

}
