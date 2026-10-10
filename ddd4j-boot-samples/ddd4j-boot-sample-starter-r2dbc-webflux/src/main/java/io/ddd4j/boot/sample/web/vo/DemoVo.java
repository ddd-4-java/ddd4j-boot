/**
 * Copyright (C) 2018 ddd4j (https://github.com/ddd-4-java/ddd4j).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

/**
 * 演示视图对象：承载 id、name、text 及可选文件上传字段，用于接口入参展示与校验。
 *
 * @author ddd4j
 * @since 1.0.0
 */
@Schema(description = "xxx数据传输对象")
public class DemoVo {

    /** 构造演示视图对象。 */
    public DemoVo() {
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
    /** 可选上传文件。 */
    @Schema(description = "文件")
    private MultipartFile file;

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
     * 获取描述。
     *
     * @return 描述
     */
    public String getText() {
        return text;
    }

    /**
     * 设置描述。
     *
     * @param text 描述
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * 获取上传文件。
     *
     * @return 上传文件
     */
    public MultipartFile getFile() {
        return file;
    }

    /**
     * 设置上传文件。
     *
     * @param file 上传文件
     */
    public void setFile(MultipartFile file) {
        this.file = file;
    }

}
