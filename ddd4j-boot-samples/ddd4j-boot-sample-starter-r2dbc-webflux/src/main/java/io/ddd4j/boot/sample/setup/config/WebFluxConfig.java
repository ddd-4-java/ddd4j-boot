package io.ddd4j.boot.sample.setup.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.ViewResolverRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.resource.PathResourceResolver;
import org.springframework.web.reactive.resource.LiteWebJarsResourceResolver;
import org.thymeleaf.spring6.SpringWebFluxTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.reactive.ThymeleafReactiveViewResolver;

/**
 * WebFlux 响应式 MVC 配置：装配 Thymeleaf 响应式模板解析、模板引擎、视图解析器以及静态资源处理器。
 *
 * <p>参考：<a href="https://www.cnblogs.com/niechen/p/9303451.html">WebFlux 集成 Thymeleaf 说明</a></p>
 *
 * @author ddd4j
 * @since 1.0.0
 */
@Configuration
@EnableWebFlux
public class WebFluxConfig implements WebFluxConfigurer, ApplicationContextAware {

    /**
     * 当前应用上下文，供模板解析器绑定。
     */
    private ApplicationContext applicationContext;

    /**
     * 构造 WebFlux 配置对象。
     */
    public WebFluxConfig() {
    }

    /**
     * 注册 Thymeleaf 资源模板解析器，前缀 classpath:/templates/、后缀 .html、UTF-8 编码。
     *
     * @return 模板解析器 Bean
     */
    @Bean
    public SpringResourceTemplateResolver templateResolver() {

        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setApplicationContext(applicationContext);
        templateResolver.setPrefix("classpath:/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setCharacterEncoding("UTF-8");
        return templateResolver;
    }

    /**
     * 注册响应式模板引擎并绑定模板解析器。
     *
     * @return Thymeleaf 响应式模板引擎 Bean
     */
    @Bean
    public SpringWebFluxTemplateEngine templateEngine() {
        SpringWebFluxTemplateEngine templateEngine = new SpringWebFluxTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        return templateEngine;
    }

    /**
     * 注册响应式视图解析器并绑定模板引擎。
     *
     * @return Thymeleaf 响应式视图解析器 Bean
     */
    @Bean
    public ThymeleafReactiveViewResolver viewResolver() {
        ThymeleafReactiveViewResolver viewResolver = new ThymeleafReactiveViewResolver();
        viewResolver.setTemplateEngine(templateEngine());
        return viewResolver;
    }

    // order matters; cache will find first and render.

    /**
     * 注册视图解析器，顺序生效以保证缓存优先命中并渲染。
     *
     * @param registry 视图解析器注册表
     */
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.viewResolver(viewResolver());
    }

    /**
     * 注册静态资源处理器：/assets 与 /resources classpath 目录，/webjars 走 LiteWebJars 与路径解析链。
     *
     * @param registry 资源处理器注册表
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/assets/**").addResourceLocations("classpath:/static/assets/");
        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/")
                .resourceChain(false).addResolver(new LiteWebJarsResourceResolver())
                .addResolver(new PathResourceResolver());
    }

    /*
     * @Bean public WebHandler webHandler(ApplicationContext applicationContext) {
     * DispatcherHandler dispatcherHandler = new
     * DispatcherHandler(applicationContext); return dispatcherHandler; }
     */

    /**
     * 注入应用上下文回调。
     *
     * @param applicationContext 当前应用上下文
     * @throws BeansException 上下文注入失败
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
