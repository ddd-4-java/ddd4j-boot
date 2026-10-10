package io.ddd4j.boot.sample.demo.domain.model.entity;

import io.ddd4j.core.ddd.model.Entity;

/**
 * Demo实体（领域层）
 */
public class DemoEntity implements Entity<Long> {
    /**
     * 构造 DemoEntity 实例。
     *
     */
    public DemoEntity() {
    }

    @Override
    public Long id() {
        return id;
    }

    /** 主键 ID。 */
    private Long id;

    /** 名称。 */
    private String name;

    /** 简介。 */
    private String intro;

    /** 显示顺序。 */
    private Integer orderBy;

    /** 状态。 */
    private Integer status;

    /** 创建时间。 */
    private java.time.LocalDateTime createTime;

    /** 更新时间。 */
    private java.time.LocalDateTime updateTime;

    /**
     * 获取Id。
     *
     * @return Id
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置Id。
     *
     * @param id Id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取Name。
     *
     * @return Name
     */
    public String getName() {
        return name;
    }

    /**
     * 设置Name。
     *
     * @param name Name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取Intro。
     *
     * @return Intro
     */
    public String getIntro() {
        return intro;
    }

    /**
     * 设置Intro。
     *
     * @param intro Intro
     */
    public void setIntro(String intro) {
        this.intro = intro;
    }

    /**
     * 获取OrderBy。
     *
     * @return OrderBy
     */
    public Integer getOrderBy() {
        return orderBy;
    }

    /**
     * 设置OrderBy。
     *
     * @param orderBy OrderBy
     */
    public void setOrderBy(Integer orderBy) {
        this.orderBy = orderBy;
    }

    /**
     * 获取Status。
     *
     * @return Status
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置Status。
     *
     * @param status Status
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取CreateTime。
     *
     * @return CreateTime
     */
    public java.time.LocalDateTime getCreateTime() {
        return createTime;
    }

    /**
     * 设置CreateTime。
     *
     * @param createTime CreateTime
     */
    public void setCreateTime(java.time.LocalDateTime createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取UpdateTime。
     *
     * @return UpdateTime
     */
    public java.time.LocalDateTime getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置UpdateTime。
     *
     * @param updateTime UpdateTime
     */
    public void setUpdateTime(java.time.LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
