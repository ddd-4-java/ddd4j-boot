/**
 * Copyright (C) 2018 ddd4j (https://github.com/ddd-4-java/ddd4j).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.repository.entities;

import io.ddd4j.core.ddd.model.Entity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 仓储层演示实体：实现 ddd4j {@link Entity} 接口，以 Lombok 注解生成访问器与链式设置方法。
 *
 * @author ddd4j
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
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

    /** 主键标识（字符串形式，读取时转换为 Long）。 */
    private String id;

    /** 名称字段。 */
    private String name;

    /** 文本内容字段。 */
    private String text;

}
