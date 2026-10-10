/**
 *
 */
package io.ddd4j.boot.sample.web.controller;

import io.ddd4j.annotation.BusinessType;
import io.ddd4j.annotation.api.ApiOperationLog;
import io.ddd4j.core.ApiRestResponse;
import io.ddd4j.web.webmvc.controller.BaseController;
import io.ddd4j.boot.sample.entity.DemoEntity;
import io.ddd4j.boot.sample.service.IDemoService;
import io.ddd4j.boot.sample.setup.LogConstant;
import io.ddd4j.boot.sample.web.dto.DemoDTO;
import io.ddd4j.boot.sample.web.dto.DemoNewDTO;
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
 * <p>
 * Demo示例表 前端控制器
 * </p>
 *
 * @author wandl
 * @since 2023-08-06
 */
@RestController
@RequestMapping("demo")
public class DemoController extends BaseController {

    @Autowired
    private IDemoService demoService;

    /**
     * 构造 DemoController 实例。
     *
     * @param messageSource 国际化消息源
     * @param beanMapper beanMapper
     */
    @Autowired
    public DemoController(NestedMessageSource messageSource, com.github.dozermapper.core.Mapper beanMapper) {
        super(messageSource, beanMapper);
    }

    /**
     * 增加逻辑实现
     *
     * @param dto 数据传输对象
     * @return 处理结果
     */
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
     *
     * @param demoVo Demo 视图对象
     * @return 处理结果
     * @throws Exception 执行过程中可能抛出的异常
     */
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
     *
     * @param ids 标识 ID 集合
     * @param request 请求对象
     * @return 删除结果
     * @throws Exception 执行过程中可能抛出的异常
     */
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
     * 获取DemoService。
     *
     * @return DemoService
     */
    public IDemoService getDemoService() {
        return demoService;
    }

    /**
     * 设置DemoService。
     *
     * @param demoService DemoService
     */
    public void setDemoService(IDemoService demoService) {
        this.demoService = demoService;
    }

}
