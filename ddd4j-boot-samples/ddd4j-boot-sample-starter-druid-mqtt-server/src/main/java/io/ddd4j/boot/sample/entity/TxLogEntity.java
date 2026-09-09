package io.ddd4j.boot.sample.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 事务日志
 */
@TableName("t_txlog")
@Data
public class TxLogEntity {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String txLogId;

    private String content;

    private Date date;

}