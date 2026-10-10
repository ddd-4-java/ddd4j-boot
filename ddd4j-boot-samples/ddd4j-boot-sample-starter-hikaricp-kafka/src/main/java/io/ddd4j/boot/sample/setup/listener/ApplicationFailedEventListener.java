package io.ddd4j.boot.sample.setup.listener;

import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationListener;

/**
 * Spring Boot 启动失败事件监听类，对启动异常做统一处理。
 * <p>
 * ApplicationFailedEvent：Spring Boot 应用启动失败时触发的事件。
 */
public class ApplicationFailedEventListener implements ApplicationListener<ApplicationFailedEvent> {

    /**
     * 构造启动失败事件监听器实例。
     */
    public ApplicationFailedEventListener() {
    }

    /**
     * 处理启动失败事件，取出异常并交由统一方法处理。
     *
     * @param event 应用启动失败事件
     */
    @Override
    public void onApplicationEvent(ApplicationFailedEvent event) {
        Throwable throwable = event.getException();
        handleThrowable(throwable);
    }

    /*处理异常*/
    private void handleThrowable(Throwable throwable) {
    }

}
