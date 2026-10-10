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
 *
 * <p>监听 {@code ApplicationEnvironmentPreparedEvent}：此时对应的 Environment 已经准备完毕，
 * 但上下文 context 还没有创建。监听器遍历全部属性源并逐条打印名称、来源与类型，便于启动期排查配置加载顺序。</p>
 *
 * @author <a href="https://github.com/wandl">wandl</a>
 * @since 1.0
 */
public class ApplicationEnvironmentPreparedEventListener implements
        ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    /**
     * 日志记录器，用于输出属性源调试信息。
     */
    private Logger logger = LoggerFactory.getLogger(ApplicationEnvironmentPreparedEventListener.class);

    /**
     * 无参构造，保持 Spring 监听器默认实例化语义。
     */
    public ApplicationEnvironmentPreparedEventListener() {
    }

    /**
     * 环境准备完成事件回调：遍历属性源集合，逐条记录属性源名称、来源对象与类型。
     *
     * @param event Spring Boot 环境准备完成事件，携带已就绪的 {@link ConfigurableEnvironment}
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
