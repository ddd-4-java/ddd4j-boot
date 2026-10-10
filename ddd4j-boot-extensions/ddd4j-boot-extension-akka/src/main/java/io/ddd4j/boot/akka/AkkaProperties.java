/**
 * Copyright (C) 2018 redacted-legacy-family (http://redacted-legacy-family.io).
 * All Rights Reserved.
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */

package io.ddd4j.boot.akka;

/**
 * Akka 配置属性（Actor 系统名称与自动装配开关）。
 */
public class AkkaProperties {

    /**
     * Actor 系统名称，默认 {@code ddd4j-akka-system}。
     */
    private String name = "ddd4j-akka-system";

    /**
     * 是否启用 Akka 自动装配（默认 true）。
     */
    private boolean enabled = true;

    /**
     * 显式无参构造器，供 Spring 绑定 Akka 配置。
     */
    public AkkaProperties() {
    }

    /** 获取 Actor 系统名称。
     * @return 系统名称 */
    public String getName() {
        return name;
    }

    /** 设置 Actor 系统名称。
     * @param name 系统名称 */
    public void setName(String name) {
        this.name = name;
    }

    /** 是否启用 Akka 自动装配。
     * @return 启用返回 {@code true} */
    public boolean isEnabled() {
        return enabled;
    }

    /** 设置是否启用 Akka 自动装配。
     * @param enabled 是否启用 */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
