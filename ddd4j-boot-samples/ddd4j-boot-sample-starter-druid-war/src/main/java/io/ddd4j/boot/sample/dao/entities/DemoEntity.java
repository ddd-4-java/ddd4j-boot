package io.ddd4j.boot.sample.dao.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.ddd4j.core.ddd.model.Entity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * Demo示例表
 * </p>
 *
 * @author wandl
 * @since 2023-08-06
 */
@Data
@Accessors(chain = true)
@TableName("t_demo")
public class DemoEntity implements Entity<Long> {

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
     * 构造 Demo 示例实体对象（显式无参构造器，与编译器生成的默认构造器等价）。
     */
    public DemoEntity() {
    }

    /**
     * 返回当前实体的标识主键。
     *
     * @return 实体主键 ID
     */
    @Override
    public Long id() {
        return id;
    }

}
