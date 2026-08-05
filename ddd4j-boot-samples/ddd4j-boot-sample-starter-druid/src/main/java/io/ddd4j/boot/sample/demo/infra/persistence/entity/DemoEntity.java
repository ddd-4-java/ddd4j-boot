package io.ddd4j.boot.sample.demo.infra.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.ddd4j.core.ddd.model.Entity;

/**
 * Demo实体（持久化层）
 */
@TableName("t_demo")
public class DemoEntity implements Entity<Long> {

    @Override
    public Long id() {
        return id;
    }

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("name")
    private String name;

    @TableField("intro")
    private String intro;

    @TableField("order_by")
    private Integer orderBy;

    @TableField("`status`")
    private Integer status;

    @TableField("create_time")
    private java.time.LocalDateTime createTime;

    @TableField("update_time")
    private java.time.LocalDateTime updateTime;

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

    public java.time.LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(java.time.LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public java.time.LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(java.time.LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
