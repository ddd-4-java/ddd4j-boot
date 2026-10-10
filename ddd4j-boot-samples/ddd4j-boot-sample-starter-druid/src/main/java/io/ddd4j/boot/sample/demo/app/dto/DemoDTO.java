package io.ddd4j.boot.sample.demo.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;


import java.time.LocalDateTime;

/**
 * Demo数据传输对象
 */
@Schema(description = "Demo信息")

public class DemoDTO {
    /**
     * 构造 DemoDTO 实例。
     *
     */
    public DemoDTO() {
    }

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


    /**
     * 获取ID。
     *
     * @return ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置ID。
     *
     * @param id ID
     */
    public void setId(Long id) {
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
    public String getIntro() {
        return intro;
    }

    /**
     * 设置描述。
     *
     * @param intro 描述
     */
    public void setIntro(String intro) {
        this.intro = intro;
    }

    /**
     * 获取显示顺序。
     *
     * @return 显示顺序
     */
    public Integer getOrderBy() {
        return orderBy;
    }

    /**
     * 设置显示顺序。
     *
     * @param orderBy 显示顺序
     */
    public void setOrderBy(Integer orderBy) {
        this.orderBy = orderBy;
    }

    /**
     * 获取状态（0:禁用|1:可用）。
     *
     * @return 状态（0:禁用|1:可用）
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置状态（0:禁用|1:可用）。
     *
     * @param status 状态（0:禁用|1:可用）
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取创建时间。
     *
     * @return 创建时间
     */
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间。
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取更新时间。
     *
     * @return 更新时间
     */
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置更新时间。
     *
     * @param updateTime 更新时间
     */
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}

