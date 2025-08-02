package io.hiwepy.boot.sample.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 事务日志
 */
@TableName("t_txlog")
@Data
@EqualsAndHashCode(callSuper = true)
public class TxLogEntity extends Model<TxLogEntity> {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String txLogId;

    private String content;

    private Date date;

}