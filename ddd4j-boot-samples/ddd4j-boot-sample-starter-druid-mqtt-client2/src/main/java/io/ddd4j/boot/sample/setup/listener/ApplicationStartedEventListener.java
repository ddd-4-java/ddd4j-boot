package io.ddd4j.boot.sample.setup.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationListener;

/**
 * spring boot 启动监听类
 * ApplicationStartedEvent：spring boot启动开始时执行的事件
 */
@Slf4j
public class ApplicationStartedEventListener implements ApplicationListener<ApplicationStartingEvent> {


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
        log.info("==MyApplicationStartedEventListener=={}", app.getMainApplicationClass());
    }

}
