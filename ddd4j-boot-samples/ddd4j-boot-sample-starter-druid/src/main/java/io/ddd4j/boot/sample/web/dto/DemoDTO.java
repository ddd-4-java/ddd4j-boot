/**
 * Copyright (C) 2018 ddd4j (https://github.com/ddd-4-java/ddd4j).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;


/**
 * DemoDTO 类
 */
@Schema(description = "xxx数据传输对象")
public class DemoDTO {
    /**
     * 构造 DemoDTO 实例。
     *
     */
    public DemoDTO() {
    }

    @Schema(description = "xxID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @Schema(description = "xx名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "名称必填")
    private String name;

    @Schema(description = "xx描述", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "描述必填")
    private String text;

    /**
     * 获取xxID。
     *
     * @return xxID
     */
    public String getId() {
        return id;
    }

    /**
     * 设置xxID。
     *
     * @param id xxID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取xx名称。
     *
     * @return xx名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置xx名称。
     *
     * @param name xx名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取xx描述。
     *
     * @return xx描述
     */
    public String getText() {
        return text;
    }

    /**
     * 设置xx描述。
     *
     * @param text xx描述
     */
    public void setText(String text) {
        this.text = text;
    }

}
