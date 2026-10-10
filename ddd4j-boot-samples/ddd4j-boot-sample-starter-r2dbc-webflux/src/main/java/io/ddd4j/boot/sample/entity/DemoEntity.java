/**
 * Copyright (C) 2018 ddd4j (https://github.com/ddd-4-java/ddd4j).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.entity;

import io.ddd4j.core.ddd.model.Entity;

/**
 * 演示实体：实现 ddd4j {@code Entity} 接口，承载 id、name、text 三个演示字段。
 *
 * @author ddd4j
 * @since 1.0.0
 */
public class DemoEntity implements Entity<Long> {

    /**
     * 构造演示实体。
     */
    public DemoEntity() {
    }

    /**
     * 返回实体标识，将字符串形式的 id 解析为 {@link Long}。
     *
     * @return 实体主键
     */
    @Override
    public Long id() {
        return Long.valueOf(id);
    }

    private static final long serialVersionUID = 6189820231775242317L;

    /**
     * 主键标识（字符串形式，读取时转换为 Long）。
     */
    private String id;

    /**
     * 名称字段。
     */
    private String name;

    /**
     * 文本内容字段。
     */
    private String text;

    /**
     * 获取主键标识。
     *
     * @return 主键标识
     */
    public String getId() {
        return id;
    }

    /**
     * 设置主键标识。
     *
     * @param id 主键标识
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取名称。
     *
     * @return 名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置名称。
     *
     * @param name 名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取文本内容。
     *
     * @return 文本内容
     */
    public String getText() {
        return text;
    }

    /**
     * 设置文本内容。
     *
     * @param text 文本内容
     */
    public void setText(String text) {
        this.text = text;
    }

}
