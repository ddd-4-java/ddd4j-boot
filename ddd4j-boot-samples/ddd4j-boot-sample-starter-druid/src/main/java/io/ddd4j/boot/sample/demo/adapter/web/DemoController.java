package io.ddd4j.boot.sample.demo.adapter.web;

import io.ddd4j.boot.core.ApiRestResponse;
import io.ddd4j.boot.sample.demo.app.command.CreateDemoCommand;
import io.ddd4j.boot.sample.demo.app.command.UpdateDemoCommand;
import io.ddd4j.boot.sample.demo.app.dto.DemoDTO;
import io.ddd4j.boot.sample.demo.app.service.DemoApplicationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Demo REST接口
 */
@Api(tags = "Demo管理")
@RestController
@RequestMapping("/api/demos")
@RequiredArgsConstructor
public class DemoController {
    
    private final DemoApplicationService demoApplicationService;
    
    /**
     * 创建Demo
     */
    @ApiOperation(value = "创建Demo", notes = "创建新的Demo记录")
    @PostMapping
    public ApiRestResponse<DemoDTO> createDemo(@Valid @RequestBody CreateDemoCommand command) {
        DemoDTO demo = demoApplicationService.createDemo(command);
        return ApiRestResponse.success(demo);
    }
    
    /**
     * 更新Demo
     */
    @ApiOperation(value = "更新Demo", notes = "更新指定Demo的信息")
    @PutMapping("/{id}")
    public ApiRestResponse<DemoDTO> updateDemo(
            @ApiParam(value = "Demo ID", example = "1", required = true)
            @PathVariable Long id,
            @Valid @RequestBody UpdateDemoCommand command) {
        command.setId(id);
        DemoDTO demo = demoApplicationService.updateDemo(command);
        return ApiRestResponse.success(demo);
    }
    
    /**
     * 根据ID查询Demo
     */
    @ApiOperation(value = "根据ID查询Demo", notes = "根据Demo ID查询详细信息")
    @GetMapping("/{id}")
    public ApiRestResponse<DemoDTO> getDemoById(
            @ApiParam(value = "Demo ID", example = "1", required = true)
            @PathVariable Long id) {
        DemoDTO demo = demoApplicationService.getDemoById(id);
        return ApiRestResponse.success(demo);
    }
    
    /**
     * 查询所有Demo
     */
    @ApiOperation(value = "查询所有Demo", notes = "查询所有Demo记录列表")
    @GetMapping
    public ApiRestResponse<List<DemoDTO>> getAllDemos() {
        List<DemoDTO> demos = demoApplicationService.getAllDemos();
        return ApiRestResponse.success(demos);
    }
    
    /**
     * 删除Demo
     */
    @ApiOperation(value = "删除Demo", notes = "根据ID删除指定Demo")
    @DeleteMapping("/{id}")
    public ApiRestResponse<String> deleteDemo(
            @ApiParam(value = "Demo ID", example = "1", required = true)
            @PathVariable Long id) {
        demoApplicationService.deleteDemo(id);
        return ApiRestResponse.success("删除成功");
    }
    
    /**
     * 批量删除Demo
     */
    @ApiOperation(value = "批量删除Demo", notes = "根据ID列表批量删除Demo")
    @DeleteMapping("/batch")
    public ApiRestResponse<String> deleteDemos(
            @ApiParam(value = "Demo ID列表", required = true)
            @RequestBody List<Long> ids) {
        demoApplicationService.deleteDemos(ids);
        return ApiRestResponse.success("批量删除成功");
    }
}

