package io.ddd4j.boot.sample.setup.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

import java.util.Iterator;

/**
 * Spring Boot 配置环境事件监听器。
 * <p>
 * 监听 {@code ApplicationEnvironmentPreparedEvent}：Spring Boot 对应 Environment
 * 已经准备完毕、但此时上下文 context 还没有创建的时机，遍历并打印全部属性源，
 * 便于启动阶段排查配置加载顺序。
 * </p>
 *
 * @author ： <a href="https://github.com/wandl">wandl</a>
 * @version V1.0
 */
public class ApplicationEnvironmentPreparedEventListener implements
        ApplicationListener<ApplicationEnvironmentPreparedEvent> {
    private Logger logger = LoggerFactory.getLogger(ApplicationEnvironmentPreparedEventListener.class);

    /**
     * 构造配置环境事件监听器（显式无参构造器，与编译器生成的默认构造器等价）。
     */
    public ApplicationEnvironmentPreparedEventListener() {
    }

    /**
     * 环境就绪事件回调：逐个打印容器内的属性源信息。
     *
     * @param event 环境就绪事件
     */
    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {

        ConfigurableEnvironment envi = event.getEnvironment();
        MutablePropertySources mps = envi.getPropertySources();
        if (mps != null) {
            Iterator<PropertySource<?>> iter = mps.iterator();
            while (iter.hasNext()) {
                PropertySource<?> ps = iter.next();
                logger
                        .info("ps.getName:{};ps.getSource:{};ps.getClass:{}", ps.getName(), ps.getSource(), ps.getClass());
            }
        }
    }

}
