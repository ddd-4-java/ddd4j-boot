package io.ddd4j.boot.sample.demo.infra.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.ddd4j.boot.sample.demo.infra.persistence.entity.DemoEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * Demo Mapper（MyBatis Plus）
 */
@Mapper
public interface DemoMapper extends BaseMapper<DemoEntity> {
}

