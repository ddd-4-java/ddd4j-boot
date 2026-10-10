/**
 * Copyright (C) 2018 ddd4j (https://github.com/easy4j).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.ddd4j.boot.sample.entity.DemoEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * Demo 示例表 MyBatis-Plus Mapper 接口，提供基础增删改查能力。
 *
 * @since 2023-08-06
 */
@Mapper
public interface DemoMapper extends BaseMapper<DemoEntity> {


}
