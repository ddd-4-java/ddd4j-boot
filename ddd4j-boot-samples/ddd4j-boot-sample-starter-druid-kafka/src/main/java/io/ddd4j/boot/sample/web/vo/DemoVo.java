/**
 * Copyright (C) 2018 ddd4j (https://github.com/ddd-4-java/ddd4j).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.web.vo;

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

/**
 * Demo 示例页面视图对象。
 * <p>
 * 承载页面回显所需的标识、名称、描述与附件文件，
 * 字段级 {@code @Schema} 描述同步生成接口文档，并配合校验注解完成入参约束。
 * </p>
 */
@Schema(description = "xxx数据传输对象")
public class DemoVo {

    @Schema(description = "xxID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;
    @Schema(description = "xx名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "名称必填")
    private String name;
    @Schema(description = "xx描述", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "描述必填")
    private String text;
    @Schema(description = "文件")
    @NotNull
    private MultipartFile file;

    /**
     * 构造 Demo 视图对象（显式无参构造器，与编译器生成的默认构造器等价）。
     */
    public DemoVo() {
    }

    /**
     * 返回标识 ID。
     * @return 标识 ID */
    public String getId() {
        return id;
    }

    /**
     * 设置标识 ID。
     * @param id 标识 ID */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 返回名称。
     * @return 名称 */
    public String getName() {
        return name;
    }

    /**
     * 设置名称。
     * @param name 名称 */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 返回描述文本。
     * @return 描述文本 */
    public String getText() {
        return text;
    }

    /**
     * 设置描述文本。
     * @param text 描述文本 */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * 返回上传文件。
     * @return 上传文件 */
    public MultipartFile getFile() {
        return file;
    }

    /**
     * 设置上传文件。
     * @param file 上传文件 */
    public void setFile(MultipartFile file) {
        this.file = file;
    }

}
