/**
 * Copyright (C) 2018 ddd4j (https://github.com/ddd-4-java/ddd4j).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 系统默认的重定向地址。
 *
 * @author ddd4j
 * @since 1.0.0
 */
@Controller
public class IndexController {

    /**
     * 构造首页控制器。
     */
    public IndexController() {
    }

    /**
     * 登录成功后的默认重定向地址：可重写返回的路径进行业务系统定制
     *
     * @param request 当前 HTTP 请求
     * @param model   视图模型
     * @return 视图名称 html/index
     */
    @RequestMapping("/index")
    public String index(HttpServletRequest request, Model model) {
        return "html/index";
    }

}
