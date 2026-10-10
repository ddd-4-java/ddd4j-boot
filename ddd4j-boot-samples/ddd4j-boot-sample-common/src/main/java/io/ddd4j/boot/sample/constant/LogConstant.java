/**
 * Copyright (C) 2018 redacted-legacy-family (http://redacted-legacy-family.io).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.constant;

/**
 * 日志跟踪常量定义，供日志组件按模块与业务维度输出 MDC 键值。
 */
public class LogConstant {

    /**
     * 构造日志常量持有类。
     *
     */
    public LogConstant() {
    }

    /**
     * 模块维度日志常量。
     */
    public static class Module {

        /**
         * 构造模块常量类。
         *
         */
        public Module() {
        }

        /** Demo 模块标识。 */
        public static final String N01 = "Demo模块";
    }

    /**
     * 业务维度日志常量。
     */
    public static class BUSINESS {
        /**
         * 构造业务常量类。
         *
         */
        public BUSINESS() {
        }

        /** Demo 业务标识。 */
        public static final String N010001 = "Demo业务";
    }

}
