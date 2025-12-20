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
 */
@Mapper
public interface OrderMapper {
    
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);
    
    /**
     * 命令转领域对象
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
     * 领域对象转DTO
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
        dto.setUpdatedAt(order.getUpdateTime());
        if (order.getItems() != null) {
            dto.setItems(order.getItems().stream()
                    .map(this::toItemDTO)
                    .collect(java.util.stream.Collectors.toList()));
        }
        return dto;
    }
    
    /**
     * 地址转DTO
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
     * 订单项转DTO
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
     * 订单列表转DTO列表
     */
    List<OrderDTO> toDTOList(List<Order> orders);
}

