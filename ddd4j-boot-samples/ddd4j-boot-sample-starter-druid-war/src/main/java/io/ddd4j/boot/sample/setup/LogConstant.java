/**
 * Copyright (C) 2018 ddd4j (https://github.com/ddd-4-java/ddd4j).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.setup;

/**
 * 日志分类常量定义类。
 * <p>
 * 按“模块 / 业务”两级内嵌静态类组织日志埋点名称，
 * 便于日志采集与检索时统一归类。
 * </p>
 */
public class LogConstant {

    /**
     * 模块级日志分类常量。
     */
    public static class Module {

        /** Demo 模块日志分类名。 */
        public static final String N01 = "Demo模块";

        /**
         * 构造模块级日志分类常量持有类（显式无参构造器，与编译器生成的默认构造器等价）。
         */
        public Module() {
        }
    }

    /**
     * 业务级日志分类常量。
     */
    public static class BUSINESS {

        /** Demo 业务日志分类名。 */
        public static final String N010001 = "Demo业务";

        /**
         * 构造业务级日志分类常量持有类（显式无参构造器，与编译器生成的默认构造器等价）。
         */
        public BUSINESS() {
        }
    }

    /**
     * 构造日志分类常量定义类（显式无参构造器，与编译器生成的默认构造器等价）。
     */
    public LogConstant() {
    }

}
