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

    public DemoController(DemoApplicationService demoApplicationService) {
        this.demoApplicationService = demoApplicationService;
    }

    /**
     * 创建Demo
     */
    @Operation(summary = "创建Demo", description = "创建新的Demo记录")
    @PostMapping
    public ApiRestResponse<DemoDTO> createDemo(@Valid @RequestBody CreateDemoCommand command) {
        DemoDTO demo = demoApplicationService.createDemo(command);
        return ApiRestResponse.success(demo);
    }

    /**
     * 更新Demo
     */
    @Operation(summary = "更新Demo", description = "更新指定Demo的信息")
    @PutMapping("/{id}")
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
     */
    @Operation(summary = "根据ID查询Demo", description = "根据Demo ID查询详细信息")
    @GetMapping("/{id}")
    public ApiRestResponse<DemoDTO> getDemoById(
            @Parameter(description = "Demo ID", example = "1", required = true)
            @PathVariable Long id) {
        DemoDTO demo = demoApplicationService.getDemoById(id);
        return ApiRestResponse.success(demo);
    }

    /**
     * 查询所有Demo
     */
    @Operation(summary = "查询所有Demo", description = "查询所有Demo记录列表")
    @GetMapping
    public ApiRestResponse<List<DemoDTO>> getAllDemos() {
        List<DemoDTO> demos = demoApplicationService.getAllDemos();
        return ApiRestResponse.success(demos);
    }

    /**
     * 删除Demo
     */
    @Operation(summary = "删除Demo", description = "根据ID删除指定Demo")
    @DeleteMapping("/{id}")
    public ApiRestResponse<String> deleteDemo(
            @Parameter(description = "Demo ID", example = "1", required = true)
            @PathVariable Long id) {
        demoApplicationService.deleteDemo(id);
        return ApiRestResponse.success("删除成功");
    }

    /**
     * 批量删除Demo
     */
    @Operation(summary = "批量删除Demo", description = "根据ID列表批量删除Demo")
    @DeleteMapping("/batch")
    public ApiRestResponse<String> deleteDemos(
            @Parameter(description = "Demo ID列表", required = true)
            @RequestBody List<Long> ids) {
        demoApplicationService.deleteDemos(ids);
        return ApiRestResponse.success("批量删除成功");
    }
}

