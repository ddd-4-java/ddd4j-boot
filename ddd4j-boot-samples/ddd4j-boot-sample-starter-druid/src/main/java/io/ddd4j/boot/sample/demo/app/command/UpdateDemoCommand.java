package io.ddd4j.boot.sample.demo.app.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * 更新Demo命令
 */
@Schema(description = "更新Demo请求")
public class UpdateDemoCommand {
    /**
     * 构造 UpdateDemoCommand 实例。
     *
     */
    public UpdateDemoCommand() {
    }

    @Schema(description = "ID", example = "1", required = true)
    @NotNull(message = "ID不能为空")
    private Long id;

    @Schema(description = "名称", example = "示例名称")
    private String name;

    @Schema(description = "描述", example = "这是一个示例描述")
    private String intro;

    @Schema(description = "显示顺序", example = "1")
    private Integer orderBy;

    @Schema(description = "状态（0:禁用|1:可用）", example = "1")
    private Integer status;

    /**
     * 获取ID。
     *
     * @return ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置ID。
     *
     * @param id ID
     */
    public void setId(Long id) {
        this.id = id;
    }

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
