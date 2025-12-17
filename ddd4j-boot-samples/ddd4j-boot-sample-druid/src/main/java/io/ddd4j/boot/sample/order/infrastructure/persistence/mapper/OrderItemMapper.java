package io.ddd4j.boot.sample.order.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.ddd4j.boot.sample.order.infrastructure.persistence.entity.OrderItemEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项Mapper（MyBatis Plus）
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItemEntity> {
}

