package io.ddd4j.boot.sample.setup.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationListener;

/**
 * spring boot 启动监听类
 * ApplicationStartedEvent：spring boot启动开始时执行的事件
 */
public class ApplicationStartedEventListener implements ApplicationListener<ApplicationStartingEvent> {

    private Logger logger = LoggerFactory.getLogger(ApplicationStartedEventListener.class);

    /**
     * 无参构造，保持 Spring Bean 默认实例化语义。
     */
    public ApplicationStartedEventListener() {
    }

    /**
     * 启动开始时打印主应用类信息。
     *
     * @param event 应用启动开始事件
     */
    @Override
    public void onApplicationEvent(ApplicationStartingEvent event) {
        SpringApplication app = event.getSpringApplication();
        logger.info("==MyApplicationStartedEventListener==" + app.getMainApplicationClass());
    }

}
