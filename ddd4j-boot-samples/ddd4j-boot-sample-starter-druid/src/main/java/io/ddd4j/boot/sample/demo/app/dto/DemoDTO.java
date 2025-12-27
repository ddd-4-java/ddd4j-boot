package io.ddd4j.boot.sample.demo.app.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Demo数据传输对象
 */
@ApiModel(description = "Demo信息")
@Data
public class DemoDTO {
    
    @ApiModelProperty(value = "ID", example = "1")
    private Long id;
    
    @ApiModelProperty(value = "名称", example = "示例名称")
    private String name;
    
    @ApiModelProperty(value = "描述", example = "这是一个示例描述")
    private String intro;
    
    @ApiModelProperty(value = "显示顺序", example = "1")
    private Integer orderBy;
    
    @ApiModelProperty(value = "状态（0:禁用|1:可用）", example = "1")
    private Integer status;
    
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
    
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;
}

