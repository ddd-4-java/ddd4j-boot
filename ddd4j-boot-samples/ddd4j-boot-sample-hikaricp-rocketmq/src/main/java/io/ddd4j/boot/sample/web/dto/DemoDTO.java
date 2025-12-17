/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "xxx数据传输对象")
@Data
public class DemoDTO {

    @Schema(description = "xxID", required = true)
    private String id;

    @Schema(description = "xx名称", required = true)
    @NotBlank(message = "名称必填")
    private String name;

    @Schema(description = "xx描述", required = true)
    @NotBlank(message = "描述必填")
    private String text;

}
