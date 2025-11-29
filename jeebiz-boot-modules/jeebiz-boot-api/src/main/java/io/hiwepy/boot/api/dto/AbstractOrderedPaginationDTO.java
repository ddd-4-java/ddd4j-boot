/**
 * Copyright (C) 2022 杭州天音计算机系统工程有限公司
 * All Rights Reserved.
 */
package io.hiwepy.boot.api.dto;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public abstract class AbstractOrderedPaginationDTO extends AbstractPaginationDTO {

    /**
     * 排序信息
     */
    @ApiModelProperty(notes = "排序信息")
    private List<OrderItem> orders;

    public List<OrderItem> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderItem> orders) {
        this.orders = orders;
    }

}