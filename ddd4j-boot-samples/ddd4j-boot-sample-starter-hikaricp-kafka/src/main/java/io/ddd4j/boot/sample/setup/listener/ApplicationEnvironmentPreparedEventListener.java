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
 * Spring Boot 配置环境事件监听。
 * <p>
 * ApplicationEnvironmentPreparedEvent：Spring Boot 对应 Enviroment 已经准备完毕，
 * 但此时上下文 context 还没有创建。事件触发时打印全部属性源信息。
 *
 * @author wandl
 * @version V1.0
 * @since 2017-11-10
 */
public class ApplicationEnvironmentPreparedEventListener implements
        ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    /**
     * 日志记录器。
     */
    private Logger logger = LoggerFactory.getLogger(ApplicationEnvironmentPreparedEventListener.class);

    /**
     * 构造配置环境事件监听器实例。
     */
    public ApplicationEnvironmentPreparedEventListener() {
    }

    /**
     * 处理配置环境准备完毕事件，遍历并打印属性源名称、来源与类型。
     *
     * @param event 应用配置环境准备事件
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
