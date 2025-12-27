package io.ddd4j.boot.sample.infrastructure.order.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.ddd4j.boot.sample.infrastructure.order.persistence.entity.OrderEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单Mapper（MyBatis Plus）
 */
@Mapper
public interface OrderMapper extends BaseMapper<OrderEntity> {
}

