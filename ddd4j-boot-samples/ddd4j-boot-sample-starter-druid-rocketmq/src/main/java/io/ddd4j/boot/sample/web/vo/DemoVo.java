/**
 * Copyright (C) 2018 ddd4j (http://ddd4j.io).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.web.vo;

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

/**
 * Demo 示例视图对象：除基础字段外附带文件上传项。
 *
 * @author ddd4j
 * @since 4.0.x
 */
@Schema(description = "xxx数据传输对象")
public class DemoVo {

    /**
     * 无参构造，供框架反序列化实例化使用。
     */
    public DemoVo() {
    }

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
     * 获取ID。
     *
     * @return ID
     */
    public String getId() {
        return id;
    }

    /**
     * 设置ID。
     *
     * @param id ID
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
