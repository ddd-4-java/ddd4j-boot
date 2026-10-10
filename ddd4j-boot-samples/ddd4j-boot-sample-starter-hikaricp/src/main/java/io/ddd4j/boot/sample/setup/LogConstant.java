/**
 * Copyright (C) 2018 ddd4j (https://github.com/easy4j).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.setup;

/**
 * 操作日志模块与业务名称常量定义类。
 */
public class LogConstant {

    /**
     * 构造日志常量类实例。
     */
    public LogConstant() {
    }

    /**
     * 模块名称常量，供操作日志注解引用。
     */
    public static class Module {

        /**
         * 构造模块常量类实例。
         */
        public Module() {
        }

        /**
         * Demo 模块名称。
         */
        public static final String N01 = "Demo模块";
    }

    /**
     * 业务名称常量，供操作日志注解引用。
     */
    public static class BUSINESS {

        /**
         * 构造业务常量类实例。
         */
        public BUSINESS() {
        }

        /**
         * Demo 业务名称。
         */
        public static final String N010001 = "Demo业务";
    }

}
