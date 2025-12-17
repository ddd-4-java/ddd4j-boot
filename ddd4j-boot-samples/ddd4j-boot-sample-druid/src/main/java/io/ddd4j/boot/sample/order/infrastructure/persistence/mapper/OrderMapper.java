package io.ddd4j.boot.sample.order.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.ddd4j.boot.sample.order.infrastructure.persistence.entity.OrderEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单Mapper（MyBatis Plus）
 */
@Mapper
public interface OrderMapper extends BaseMapper<OrderEntity> {
}

