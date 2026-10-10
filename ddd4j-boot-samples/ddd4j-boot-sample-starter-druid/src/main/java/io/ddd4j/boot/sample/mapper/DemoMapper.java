/**
 * Copyright (C) 2018 ddd4j (https://github.com/ddd-4-java/ddd4j).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.ddd4j.boot.sample.entity.DemoEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * DemoMapper 接口
 */
@Mapper
public interface DemoMapper extends BaseMapper<DemoEntity> {


}
