/**
 * Copyright (C) 2022 杭州天音计算机系统工程有限公司
 * All Rights Reserved.
 */
package io.hiwepy.boot.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public abstract class BaseDTO {

    /**
     * 请求发生的时间
     */
    @Schema(description = "请求发生的时间", hidden = true)
    private long currentTimeMillis;

}