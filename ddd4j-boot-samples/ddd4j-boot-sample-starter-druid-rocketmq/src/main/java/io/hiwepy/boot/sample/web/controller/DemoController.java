/**
 *
 */
package io.hiwepy.boot.sample.web.controller;

import io.ddd4j.annotation.BusinessType;
import io.ddd4j.annotation.api.ApiOperationLog;
import io.ddd4j.core.ApiRestResponse;
import io.ddd4j.spring.web.BaseMapperController;
import io.hiwepy.boot.sample.entity.DemoEntity;
import io.hiwepy.boot.sample.service.IDemoService;
import io.hiwepy.boot.sample.setup.LogConstant;
import io.hiwepy.boot.sample.web.dto.DemoDTO;
import io.hiwepy.boot.sample.web.dto.DemoNewDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.biz.utils.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * Demo示例表 前端控制器
 * </p>
 *
 * @author wandl
 * @since 2023-08-06
 */
@RestController
@RequestMapping("demo")
public class DemoController extends BaseMapperController {

    @Autowired
    private IDemoService demoService;

    /**
     * 增加逻辑实现
     */
    @Operation(summary = "创建xxx信息", description = "根据DemoVo创建xxx")
    @Parameter(name = "demoVo", description = "xxx数据传输对象", required = true)
    @ApiOperationLog(module = LogConstant.Module.N01, business = LogConstant.BUSINESS.N010001, opt = BusinessType.INSERT)
    @PostMapping("new")
    @ResponseBody
    public ApiRestResponse<String> newDemo(@Valid DemoNewDTO dto) {
        try {

            DemoEntity entity = new DemoEntity();

            //如果自己较少，采用手动设置方式
            entity.setName(dto.getName());
            //如果自动较多，采用对象拷贝方式；该方式不支持文件对象拷贝
            //PropertyUtils.copyProperties(DemoEntity, demoVo);

            getDemoService().save(entity);
            return success("demo.new.success");
        } catch (Exception e) {
            logException(this, e);
            return fail("demo.new.fail");
        }
    }

    /**
     * 修改逻辑实现
     */
    @Operation(summary = "修改xxx信息", description = "修改xxx")
    @Parameter(name = "demoVo", description = "xxx数据传输对象", required = true)
    @ApiOperationLog(module = LogConstant.Module.N01, business = LogConstant.BUSINESS.N010001, opt = BusinessType.UPDATE)
    @PostMapping("renew")
    @ResponseBody
    public ApiRestResponse<String> renew(@Valid DemoDTO demoVo) throws Exception {
        try {

            DemoEntity entity = new DemoEntity();

            //如果自己较少，采用手动设置方式
            entity.setName(demoVo.getName());
            //如果自动较多，采用对象拷贝方式；该方式不支持文件对象拷贝
            //PropertyUtils.copyProperties(DemoEntity, demoVo);

            getDemoService().updateById(entity);
            return success("demo.renew.success");
        } catch (Exception e) {
            logException(this, e);
            return fail("demo.renew.fail");
        }
    }

    /**
     * 删除逻辑实现
     */
    @Operation(summary = "删除xxx信息", description = "根据ID删除xxx")
    @Parameter(name = "ids", description = "ID集合，多个使用,拼接", required = true)
    @ApiOperationLog(module = LogConstant.Module.N01, business = LogConstant.BUSINESS.N010001, opt = BusinessType.DELETE)
    @PostMapping("delete")
    @ResponseBody
    public ApiRestResponse<String> delete(@RequestParam(value = "ids") String ids, HttpServletRequest request) throws Exception {
        try {
            if (ObjectUtils.isEmpty(ids)) {
                return fail("demo.delete.fail");
            }
            List<String> list = Arrays.asList(StringUtils.tokenizeToStringArray(ids));
            // 批量删除数据库配置记录
            getDemoService().removeBatchByIds(list);
            return success("demo.delete.success");
        } catch (Exception e) {
            logException(this, e);
            return fail("demo.delete.fail");
        }
    }


    public IDemoService getDemoService() {
        return demoService;
    }

    public void setDemoService(IDemoService demoService) {
        this.demoService = demoService;
    }

}
