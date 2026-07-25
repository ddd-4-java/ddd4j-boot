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
}

