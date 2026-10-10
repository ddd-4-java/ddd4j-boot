/**
 * Copyright (C) 2018 ddd4j (http://ddd4j.io).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Demo 新版数据传输对象：仅承载名称与描述两项入参。
 *
 * @author ddd4j
 * @since 4.0.x
 */
@Schema(description = "xxx数据传输对象")
@Data
public class DemoNewDTO {

    /**
     * 无参构造，供框架反序列化实例化使用。
     */
    public DemoNewDTO() {
    }

    @Schema(description = "xx名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "名称必填")
    private String name;

    @Schema(description = "xx描述", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "描述必填")
    private String text;

}
