package io.ddd4j.boot.sample.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.spring.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 事务日志实体：映射 {@code t_txlog} 表，记录事务操作流水。
 *
 * @author ddd4j
 * @since 4.0.x
 */
@TableName("t_txlog")
@Data
@EqualsAndHashCode(callSuper = true)
public class TxLogEntity extends Model<TxLogEntity> {

    /**
     * 无参构造，供框架反序列化与 ActiveRecord 模式实例化使用。
     */
    public TxLogEntity() {
    }

    /**
     * 事务日志主键。
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String txLogId;

    /**
     * 事务日志内容。
     */
    private String content;

    /**
     * 事务日志产生时间。
     */
    private Date date;

}