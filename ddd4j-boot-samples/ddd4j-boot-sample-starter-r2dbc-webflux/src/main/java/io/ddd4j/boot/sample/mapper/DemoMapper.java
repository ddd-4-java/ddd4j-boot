/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.mapper;

import io.ddd4j.boot.sample.entity.DemoEntity;
import io.ddd4j.core.mybatis.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DemoMapper extends BaseMapper<DemoEntity> {


}
