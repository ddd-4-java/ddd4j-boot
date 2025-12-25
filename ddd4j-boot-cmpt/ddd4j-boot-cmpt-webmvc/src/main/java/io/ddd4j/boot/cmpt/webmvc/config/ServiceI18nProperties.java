/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.ddd4j.boot.cmpt.webmvc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 软件服务国际化信息
 */
@ConfigurationProperties("server.i18n")
@Data
public class ServiceI18nProperties {

    /**
     * 是否启用国际化
     */
    private boolean enabled;

}
