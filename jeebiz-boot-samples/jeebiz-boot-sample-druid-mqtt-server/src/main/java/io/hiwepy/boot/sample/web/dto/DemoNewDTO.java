/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.hiwepy.boot.sample.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Schema( description = "xxx数据传输对象")
@Data
public class DemoNewDTO {

    @Schema(description = "xx名称", required = true)
    @NotBlank(message = "名称必填")
    private String name;

    @Schema(description = "xx描述", required = true)
    @NotBlank(message = "描述必填")
    private String text;

}
