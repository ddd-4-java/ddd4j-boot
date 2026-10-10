/**
 * Copyright (C) 2018 ddd4j (http://ddd4j.io).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 系统默认的重定向地址
 */
@Controller
public class IndexController {

    /**
     * 无参构造，保持 Spring Bean 默认实例化语义。
     */
    public IndexController() {
    }

    /**
     * 登录成功后的默认重定向地址：可重写返回的路径进行业务系统定制
     *
     * @param request HTTP 请求对象
     * @param model   页面模型
     * @return 视图名称 {@code html/index}
     */
    @RequestMapping("/index")
    public String index(HttpServletRequest request, Model model) {
        return "html/index";
    }

}
