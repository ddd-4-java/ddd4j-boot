package io.ddd4j.boot.sample.demo.adapter.web;

import io.ddd4j.boot.sample.demo.app.command.CreateDemoCommand;
import io.ddd4j.boot.sample.demo.app.command.UpdateDemoCommand;
import io.ddd4j.boot.sample.demo.app.dto.DemoDTO;
import io.ddd4j.boot.sample.demo.app.service.DemoApplicationService;
import io.ddd4j.core.ApiRestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Demo REST接口
 */
@Tag(name = "Demo管理", description = "Demo相关的API接口")
@RestController
@RequestMapping("/api/demos")

public class DemoController {

    private final DemoApplicationService demoApplicationService;

    /**
     * 构造 DemoController 实例。
     *
     * @param demoApplicationService demoApplicationService
     */
    public DemoController(DemoApplicationService demoApplicationService) {
        this.demoApplicationService = demoApplicationService;
    }

    /**
     * 创建Demo
     *
     * @param command 命令对象
     * @return 新增结果
     */
    public ApiRestResponse<DemoDTO> createDemo(@Valid @RequestBody CreateDemoCommand command) {
        DemoDTO demo = demoApplicationService.createDemo(command);
        return ApiRestResponse.success(demo);
    }

    /**
     * 更新Demo
     *
     * @param id 标识 ID
     * @param command 命令对象
     * @return 更新结果
     */
    public ApiRestResponse<DemoDTO> updateDemo(
            @Parameter(description = "Demo ID", example = "1", required = true)
            @PathVariable Long id,
            @Valid @RequestBody UpdateDemoCommand command) {
        command.setId(id);
        DemoDTO demo = demoApplicationService.updateDemo(command);
        return ApiRestResponse.success(demo);
    }

    /**
     * 根据ID查询Demo
     *
     * @param id 标识 ID
     * @return 查询结果
     */
    public ApiRestResponse<DemoDTO> getDemoById(
            @Parameter(description = "Demo ID", example = "1", required = true)
            @PathVariable Long id) {
        DemoDTO demo = demoApplicationService.getDemoById(id);
        return ApiRestResponse.success(demo);
    }

    /**
     * 查询所有Demo
     *
     * @return 查询结果
     */
    public ApiRestResponse<List<DemoDTO>> getAllDemos() {
        List<DemoDTO> demos = demoApplicationService.getAllDemos();
        return ApiRestResponse.success(demos);
    }

    /**
     * 删除Demo
     *
     * @param id 标识 ID
     * @return 删除结果
     */
    public ApiRestResponse<String> deleteDemo(
            @Parameter(description = "Demo ID", example = "1", required = true)
            @PathVariable Long id) {
        demoApplicationService.deleteDemo(id);
        return ApiRestResponse.success("删除成功");
    }

    /**
     * 批量删除Demo
     *
     * @param ids 标识 ID 集合
     * @return 删除结果
     */
    public ApiRestResponse<String> deleteDemos(
            @Parameter(description = "Demo ID列表", required = true)
            @RequestBody List<Long> ids) {
        demoApplicationService.deleteDemos(ids);
        return ApiRestResponse.success("批量删除成功");
    }
}

