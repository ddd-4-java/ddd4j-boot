/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.hiwepy.boot.sample.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.hiwepy.boot.sample.entity.TxLogEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TxLogMapper extends BaseMapper<TxLogEntity> {


}
