/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package com.github.hiwepy.ddd4j.jackson;

import com.github.hiwepy.ddd4j.jackson.aspect.ApiOperationLogProvider;
import com.github.hiwepy.ddd4j.jackson.aspect.DefaultApiOperationLogProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiLogAspectConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ApiOperationLogProvider apiOperationLogProvider() {
        return new DefaultApiOperationLogProvider();
    }

}
