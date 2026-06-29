package io.ddd4j.boot.sample.demo.infra.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.ddd4j.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Demo实体（持久化层）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_demo")
public class DemoEntity extends BaseEntity<DemoEntity> {

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

