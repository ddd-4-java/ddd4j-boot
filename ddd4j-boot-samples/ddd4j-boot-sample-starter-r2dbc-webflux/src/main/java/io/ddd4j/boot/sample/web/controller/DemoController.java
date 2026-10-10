/**
 *
 */
package io.ddd4j.boot.sample.web.controller;

import io.ddd4j.annotation.BusinessType;
import io.ddd4j.annotation.api.ApiOperationLog;
import io.ddd4j.boot.sample.entity.DemoEntity;
import io.ddd4j.boot.sample.service.IDemoService;
import io.ddd4j.boot.sample.setup.LogConstant;
import io.ddd4j.boot.sample.web.dto.DemoDTO;
import io.ddd4j.boot.sample.web.dto.DemoNewDTO;
import io.ddd4j.core.ApiRestResponse;
import io.ddd4j.web.webmvc.controller.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extension.context.NestedMessageSource;
import org.springframework.extension.utils.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 演示 REST 控制器：继承 {@code BaseController}，提供演示数据的新增、修改与批量删除接口。
 *
 * @author ddd4j
 * @since 1.0.0
 */
@RestController
@RequestMapping("demo")
public class DemoController extends BaseController {

    /**
     * 构造演示控制器。
     *
     * @param messageSource 嵌套消息源，供国际化文案解析
     * @param beanMapper    Dozer 对象映射器，供对象拷贝使用
     */
    public DemoController(NestedMessageSource messageSource, com.github.dozermapper.core.Mapper beanMapper) {
        super(messageSource, beanMapper);
    }

    /**
     * 演示业务服务，注入后供各接口执行持久化操作。
     */
    @Autowired
    private IDemoService demoService;

    /**
     * 新增演示数据：将传输对象转换为实体并保存，失败时返回统一失败响应。
     *
     * @param dto 新增用演示传输对象（校验必填）
     * @return 统一响应对象，成功时携带 demo.new.success 文案
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
     * 修改演示数据：按传输对象字段更新对应实体记录。
     *
     * @param demoVo 修改用演示传输对象（校验必填）
     * @return 统一响应对象，成功时携带 demo.renew.success 文案
     * @throws Exception 参数校验或处理过程中的异常
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
     * 批量删除演示数据：ids 为空直接返回失败，否则按逗号分隔后批量移除。
     *
     * @param ids     逗号拼接的主键集合
     * @param request 当前 HTTP 请求
     * @return 统一响应对象，成功时携带 demo.delete.success 文案
     * @throws Exception 删除过程中的异常
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


    /**
     * 获取演示业务服务。
     *
     * @return 演示业务服务
     */
    public IDemoService getDemoService() {
        return demoService;
    }

    /**
     * 设置演示业务服务。
     *
     * @param demoService 演示业务服务
     */
    public void setDemoService(IDemoService demoService) {
        this.demoService = demoService;
    }

}
