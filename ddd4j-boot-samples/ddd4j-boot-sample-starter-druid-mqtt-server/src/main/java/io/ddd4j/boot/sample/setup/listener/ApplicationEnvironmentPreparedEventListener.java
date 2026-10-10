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
 * <p>{@code ApplicationEnvironmentPreparedEvent}：Spring Boot 对应 Environment 已经准备完毕，
 * 但此时上下文 context 还没有创建；本监听器在此阶段遍历并打印全部属性源，便于排查配置加载顺序。</p>
 *
 * @author ddd4j
 * @since 4.0.x
 */
public class ApplicationEnvironmentPreparedEventListener implements
        ApplicationListener<ApplicationEnvironmentPreparedEvent> {
    private Logger logger = LoggerFactory.getLogger(ApplicationEnvironmentPreparedEventListener.class);

    /**
     * 无参构造，保持 Spring Bean 默认实例化语义。
     */
    public ApplicationEnvironmentPreparedEventListener() {
    }

    /**
     * 环境准备完成后逐条输出属性源名称、来源与类型。
     *
     * @param event 应用环境准备完成事件
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
