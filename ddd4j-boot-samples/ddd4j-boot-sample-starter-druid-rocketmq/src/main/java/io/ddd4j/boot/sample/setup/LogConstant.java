/**
 * Copyright (C) 2018 ddd4j (http://ddd4j.io).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.setup;

/**
 * 日志模块/业务编码常量聚合类：按「模块 → 业务」两级内嵌类组织日志编码。
 *
 * @author ddd4j
 * @since 4.0.x
 */
public class LogConstant {

    /**
     * 无参构造，常量容器类不承载实例状态。
     */
    public LogConstant() {
    }

    /**
     * 模块级日志编码常量。
     */
    public static class Module {

        /**
         * 无参构造，常量容器类不承载实例状态。
         */
        public Module() {
        }

        /**
         * Demo 模块编码。
         */
        public static final String N01 = "Demo模块";
    }

    /**
     * 业务级日志编码常量。
     */
    public static class BUSINESS {

        /**
         * 无参构造，常量容器类不承载实例状态。
         */
        public BUSINESS() {
        }

        /**
         * Demo 业务编码。
         */
        public static final String N010001 = "Demo业务";
    }

}
