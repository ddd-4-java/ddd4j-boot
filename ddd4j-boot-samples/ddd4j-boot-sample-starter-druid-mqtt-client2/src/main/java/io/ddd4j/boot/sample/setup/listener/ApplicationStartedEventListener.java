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
     * 构造启动开始事件监听器（显式无参构造器，与编译器生成的默认构造器等价）。
     */
    public ApplicationStartedEventListener() {
    }

    /**
     * 应用启动开始事件回调：打印启动主类信息。
     *
     * @param event 应用启动开始事件
     */
    @Override
    public void onApplicationEvent(ApplicationStartingEvent event) {
        SpringApplication app = event.getSpringApplication();
        logger.info("==MyApplicationStartedEventListener==" + app.getMainApplicationClass());
    }

}
