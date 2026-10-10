/**
 * Copyright (C) 2018 ddd4j (https://github.com/ddd-4-java/ddd4j).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.setup;

/**
 * 操作日志常量定义类：集中声明 {@code Module} 与 {@code BUSINESS} 两组日志分组常量。
 *
 * @author ddd4j
 * @since 1.0.0
 */
public class LogConstant {

    /**
     * 构造日志常量类。
     */
    public LogConstant() {
    }

    /**
     * 操作日志模块分组常量。
     */
    public static class Module {

        /**
         * 构造模块常量类。
         */
        public Module() {
        }

        /**
         * Demo 模块标识。
         */
        public static final String N01 = "Demo模块";
    }

    /**
     * 操作日志业务分组常量。
     */
    public static class BUSINESS {

        /**
         * 构造业务常量类。
         */
        public BUSINESS() {
        }

        /**
         * Demo 业务标识。
         */
        public static final String N010001 = "Demo业务";
    }

}
