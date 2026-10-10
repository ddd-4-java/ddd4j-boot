package io.ddd4j.boot.sample.app.order.mapper;

import io.ddd4j.boot.sample.app.order.command.CreateOrderCommand;
import io.ddd4j.boot.sample.app.order.dto.OrderDTO;
import io.ddd4j.boot.sample.domain.order.model.aggregate.Order;
import io.ddd4j.boot.sample.domain.order.model.entity.OrderItem;
import io.ddd4j.boot.sample.domain.order.model.vo.Address;
import io.ddd4j.boot.sample.domain.order.model.vo.Money;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 订单对象映射器
 *
 * <p>应用层的模型转换门面：在「命令 / DTO」与「订单聚合」之间做显式映射，
 * 是应用层与领域层之间的隔离带。批量方法 {@code toDTOList} 由 MapStruct
 * 按接口抽象生成实现，其余 default 方法承载需要业务判断的逐字段映射。</p>
 */
@Mapper
public interface OrderMapper {

    /** MapStruct 生成实现的单例入口，供应用服务直接引用。 */
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    /**
     * 命令转领域对象。
     *
     * <p>把创建订单命令中的订单项、收货地址逐项组装为领域对象后，
     * 交给订单聚合工厂 {@link Order#create} 完成校验与状态初始化；
     * 币种缺失时按 CNY 兜底。</p>
     *
     * @param command 创建订单命令
     * @param orderNo 已生成的订单编号
     * @return 新建的订单聚合根
     * @throws IllegalArgumentException 命令必填项缺失导致聚合工厂校验失败时抛出
     */
    default Order toDomain(CreateOrderCommand command, String orderNo) {
        List<OrderItem> items = command.getItems().stream()
                .map(item -> new OrderItem(
                        item.getProductId(),
                        item.getProductName(),
                        item.getQuantity(),
                        new Money(item.getUnitPrice(), item.getCurrency() != null ? item.getCurrency() : "CNY")
                ))
                .collect(java.util.stream.Collectors.toList());

        // 转换地址
        CreateOrderCommand.AddressCommand addrCmd = command.getShippingAddress();
        Address address = new Address(
                addrCmd.getProvince(),
                addrCmd.getCity(),
                addrCmd.getDistrict(),
                addrCmd.getDetail(),
                addrCmd.getZipCode()
        );

        return Order.create(orderNo, command.getUserId(), address, items);
    }

    /**
     * 领域对象转 DTO。
     *
     * @param order 订单聚合根，为 {@code null} 时返回 {@code null}
     * @return 订单 DTO，含订单项与收货地址
     */
    default OrderDTO toDTO(Order order) {
        if (order == null) {
            return null;
        }
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderNo(order.getOrderNo());
        dto.setUserId(order.getUserId());
        dto.setStatus(order.getStatus());
        dto.setStatusDescription(order.getStatus() != null ? order.getStatus().getDescription() : null);
        dto.setTotalAmount(order.getTotalAmount() != null ? order.getTotalAmount().amount() : null);
        dto.setCurrency(order.getTotalAmount() != null ? order.getTotalAmount().currency() : null);
        dto.setShippingAddress(toAddressDTO(order.getShippingAddress()));
        dto.setRemark(order.getRemark());
        dto.setPaidTime(order.getPaidTime());
        dto.setShippedTime(order.getShippedTime());
        dto.setDeliveredTime(order.getDeliveredTime());
        dto.setCreatedAt(order.getCreateTime());
        dto.setUpdatedAt(order.getCreateTime());
        if (order.getItems() != null) {
            dto.setItems(order.getItems().stream()
                    .map(this::toItemDTO)
                    .collect(java.util.stream.Collectors.toList()));
        }
        return dto;
    }

    /**
     * 地址转 DTO。
     *
     * @param address 地址值对象，为 {@code null} 时返回 {@code null}
     * @return 地址 DTO，含拼接后的完整地址
     */
    default OrderDTO.AddressDTO toAddressDTO(Address address) {
        if (address == null) {
            return null;
        }
        OrderDTO.AddressDTO dto = new OrderDTO.AddressDTO();
        dto.setProvince(address.province());
        dto.setCity(address.city());
        dto.setDistrict(address.district());
        dto.setDetail(address.detail());
        dto.setZipCode(address.zipCode());
        dto.setFullAddress(address.getFullAddress());
        return dto;
    }

    /**
     * 订单项转 DTO。
     *
     * @param item 订单项实体，为 {@code null} 时返回 {@code null}
     * @return 订单项 DTO，金额取数值部分、币种单独映射
     */
    default OrderDTO.OrderItemDTO toItemDTO(OrderItem item) {
        if (item == null) {
            return null;
        }
        OrderDTO.OrderItemDTO dto = new OrderDTO.OrderItemDTO();
        dto.setId(item.getId());
        dto.setProductId(item.getProductId());
        dto.setProductName(item.getProductName());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice() != null ? item.getUnitPrice().amount() : null);
        dto.setTotalPrice(item.getTotalPrice() != null ? item.getTotalPrice().amount() : null);
        dto.setCurrency(item.getUnitPrice() != null ? item.getUnitPrice().currency() : null);
        return dto;
    }

    /**
     * 订单列表转 DTO 列表（由 MapStruct 生成实现）。
     *
     * @param orders 订单聚合根列表
     * @return 对应的订单 DTO 列表
     */
    List<OrderDTO> toDTOList(List<Order> orders);
}

