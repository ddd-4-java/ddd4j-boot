/**
 * Copyright (C) 2018 ddd4j (https://github.com/ddd-4-java/ddd4j).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.ddd4j.boot.sample.entity.DemoEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * Demo 示例表的数据访问接口。
 * <p>
 * 基于 MyBatis-Plus 的 {@code BaseMapper} 继承单表增删改查能力，
 * 由 {@code @Mapper} 注解标记并交由容器扫描注册。
 * </p>
 */
@Mapper
public interface DemoMapper extends BaseMapper<DemoEntity> {


}
