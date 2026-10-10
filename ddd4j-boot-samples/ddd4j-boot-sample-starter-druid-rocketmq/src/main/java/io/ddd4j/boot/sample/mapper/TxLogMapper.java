/**
 * Copyright (C) 2018 ddd4j (http://ddd4j.io).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.ddd4j.boot.sample.entity.TxLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 事务日志表 MyBatis-Plus Mapper，提供基础增删改查能力。
 *
 * @author ddd4j
 * @since 4.0.x
 */
@Mapper
public interface TxLogMapper extends BaseMapper<TxLogEntity> {


}
