/**
 * Copyright (C) 2018 ddd4j (https://github.com/ddd-4-java/ddd4j).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Demo 示例修改请求数据传输对象。
 * <p>
 * 携带主键、名称与描述三个字段，字段级校验注解用于入参合法性检查。
 * </p>
 */
@Schema(description = "xxx数据传输对象")
@Data
public class DemoDTO {

    @Schema(description = "xxID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @Schema(description = "xx名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "名称必填")
    private String name;

    @Schema(description = "xx描述", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "描述必填")
    private String text;

    /**
     * 构造 Demo 修改请求数据传输对象（显式无参构造器，与编译器生成的默认构造器等价）。
     */
    public DemoDTO() {
    }

}
