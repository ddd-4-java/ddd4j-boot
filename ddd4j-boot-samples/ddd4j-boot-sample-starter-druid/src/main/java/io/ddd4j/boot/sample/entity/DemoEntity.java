package io.ddd4j.boot.sample.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.ddd4j.core.ddd.model.Entity;



/**
 * <p>
 * Demo示例表
 * </p>
 *
 * @author wandl
 * @since 2023-08-06
 */


@TableName("t_demo")
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

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 名称
     */
    @TableField("name")
    private String name;

    /**
     * 简述
     */
    @TableField("intro")
    private String intro;

    /**
     * 显示顺序
     */
    @TableField("order_by")
    private Integer orderBy;

    /**
     * 状态（0:禁用|1:可用）
     */
    @TableField("`status`")
    private Integer status;

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
}
