/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.hiwepy.boot.autoconfigure;

import cn.dev33.satoken.strategy.SaAnnotationStrategy;
import io.hiwepy.boot.autoconfigure.satoken.handler.SaMixCheckLoginHandler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotatedElementUtils;


/**
 *
 */
@Configuration
public class SaTokenEnhanceAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        // 重写Sa-Token的注解处理器，增加注解合并功能
        SaAnnotationStrategy.instance.getAnnotation = AnnotatedElementUtils::getMergedAnnotation;
    }

    @Bean
    public SaMixCheckLoginHandler saMixCheckLoginHandler() {
        return new SaMixCheckLoginHandler();
    }

}
