/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.hiwepy.boot.sample.mapper;

import io.hiwepy.boot.api.mybatis.mapper.BaseMapper;
import io.hiwepy.boot.sample.entity.DemoEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DemoMapper extends BaseMapper<DemoEntity> {


}
