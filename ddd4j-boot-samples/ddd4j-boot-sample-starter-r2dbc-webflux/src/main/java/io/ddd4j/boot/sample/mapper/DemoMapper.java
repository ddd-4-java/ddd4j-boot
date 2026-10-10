/**
 * Copyright (C) 2018 ddd4j (https://github.com/ddd-4-java/ddd4j).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.mapper;

import io.ddd4j.boot.sample.entity.DemoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 演示数据 Mapper 接口：继承 MyBatis-Plus {@code BaseMapper}，提供 {@code DemoEntity} 的基础数据访问。
 *
 * @author ddd4j
 * @since 1.0.0
 */
@Mapper
public interface DemoMapper extends BaseMapper<DemoEntity> {


}
