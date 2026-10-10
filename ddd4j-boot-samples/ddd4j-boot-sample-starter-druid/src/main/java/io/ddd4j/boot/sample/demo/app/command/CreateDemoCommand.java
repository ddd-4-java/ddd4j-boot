package io.ddd4j.boot.sample.demo.app.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * 创建Demo命令
 */
@Schema(description = "创建Demo请求")
public class CreateDemoCommand {
    /**
     * 构造 CreateDemoCommand 实例。
     *
     */
    public CreateDemoCommand() {
    }

    @Schema(description = "名称", example = "示例名称", required = true)
    @NotBlank(message = "名称不能为空")
    private String name;

    @Schema(description = "描述", example = "这是一个示例描述", required = true)
    @NotBlank(message = "描述不能为空")
    private String intro;

    @Schema(description = "显示顺序", example = "1")
    private Integer orderBy;

    @Schema(description = "状态（0:禁用|1:可用）", example = "1")
    private Integer status;

    /**
     * 获取名称。
     *
     * @return 名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取描述。
     *
     * @return 描述
     */
    public String getIntro() {
        return intro;
    }

    /**
     * 获取显示顺序。
     *
     * @return 显示顺序
     */
    public Integer getOrderBy() {
        return orderBy;
    }

    /**
     * 获取状态（0:禁用|1:可用）。
     *
     * @return 状态（0:禁用|1:可用）
     */
    public Integer getStatus() {
        return status;
    }
}
