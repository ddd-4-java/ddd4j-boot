package io.ddd4j.boot.sample.setup.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationListener;

/**
 * Spring Boot 启动监听类。
 *
 * <p>监听 {@code ApplicationStartingEvent}：Spring Boot 启动开始时触发，此时尚未创建应用上下文。
 * 回调中打印主应用类信息，便于确认启动入口。</p>
 *
 * @author ddd4j
 * @since 4.0.x
 */
public class ApplicationStartedEventListener implements ApplicationListener<ApplicationStartingEvent> {

    /**
     * 日志记录器，用于输出启动阶段信息。
     */
    private Logger logger = LoggerFactory.getLogger(ApplicationStartedEventListener.class);

    /**
     * 无参构造，保持 Spring 监听器默认实例化语义。
     */
    public ApplicationStartedEventListener() {
    }

    /**
     * 应用启动开始事件回调：打印主应用类信息。
     *
     * @param event 应用启动开始事件，携带 {@link SpringApplication} 实例
     */
    @Override
    public void onApplicationEvent(ApplicationStartingEvent event) {
        SpringApplication app = event.getSpringApplication();
        logger.info("==MyApplicationStartedEventListener==" + app.getMainApplicationClass());
    }

}
