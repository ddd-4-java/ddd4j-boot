package io.ddd4j.boot.sample.demo.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;


import java.time.LocalDateTime;

/**
 * Demo数据传输对象
 */
@Schema(description = "Demo信息")

public class DemoDTO {

    @Schema(description = "ID", example = "1")
    private Long id;

    @Schema(description = "名称", example = "示例名称")
    private String name;

    @Schema(description = "描述", example = "这是一个示例描述")
    private String intro;

    @Schema(description = "显示顺序", example = "1")
    private Integer orderBy;

    @Schema(description = "状态（0:禁用|1:可用）", example = "1")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public Integer getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(Integer orderBy) {
        this.orderBy = orderBy;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}

