package io.ddd4j.web.validation.constraintvalidators;

import io.ddd4j.web.validation.constraints.AllowedValues;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

/**
 * 验证值是否在指定范围内
 *
 * @author hiwepy
 * @since 2021-03-08
 */
public class AllowedValuesValidator implements ConstraintValidator<AllowedValues, String> {

    List<String> allowedValues;
    boolean nullable;

    @Override
    public void initialize(AllowedValues annotation) {
        nullable = annotation.nullable();
        allowedValues = Arrays.asList(annotation.values());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (nullable && !StringUtils.hasText(value)) {
            return true;
        }
        return allowedValues.contains(value);
    }
}
