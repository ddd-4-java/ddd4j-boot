package io.ddd4j.boot.sample.demo.app.command;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 更新Demo命令
 */
@ApiModel(description = "更新Demo请求")
@Data
public class UpdateDemoCommand {
    
    @ApiModelProperty(value = "ID", example = "1", required = true)
    @NotNull(message = "ID不能为空")
    private Long id;
    
    @ApiModelProperty(value = "名称", example = "示例名称")
    private String name;
    
    @ApiModelProperty(value = "描述", example = "这是一个示例描述")
    private String intro;
    
    @ApiModelProperty(value = "显示顺序", example = "1")
    private Integer orderBy;
    
    @ApiModelProperty(value = "状态（0:禁用|1:可用）", example = "1")
    private Integer status;
}

