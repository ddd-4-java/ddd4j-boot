/**
 * Copyright (C) 2018 ddd4j (https://github.com/ddd-4-java/ddd4j).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 演示数据传输对象：承载 id、name、text 字段，用于修改接口入参。
 *
 * @author ddd4j
 * @since 1.0.0
 */
@Schema(description = "xxx数据传输对象")
@Data
public class DemoDTO {

    /**
     * 构造演示传输对象。
     */
    public DemoDTO() {
    }

    /** 主键标识（必填）。 */
    @Schema(description = "xxID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    /** 名称（必填，不允许为空白）。 */
    @Schema(description = "xx名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "名称必填")
    private String name;

    /** 描述（必填，不允许为空白）。 */
    @Schema(description = "xx描述", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "描述必填")
    private String text;

}
