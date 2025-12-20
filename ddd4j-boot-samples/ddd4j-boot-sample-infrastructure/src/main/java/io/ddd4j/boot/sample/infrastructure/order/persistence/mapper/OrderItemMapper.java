package io.ddd4j.boot.sample.infrastructure.order.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.ddd4j.boot.sample.infrastructure.order.persistence.entity.OrderItemEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项Mapper（MyBatis Plus）
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItemEntity> {
}

