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
     * 构造默认首页控制器（显式无参构造器，与编译器生成的默认构造器等价）。
     */
    public IndexController() {
    }

    /**
     * 登录成功后的默认重定向地址：可重写返回的路径进行业务系统定制
     *
     * @param request 当前 HTTP 请求
     * @param model   视图模型
     * @return 默认首页视图名称
     */
    @RequestMapping("/index")
    public String index(HttpServletRequest request, Model model) {
        return "html/index";
    }

}
