package io.ddd4j.boot.sample.setup.listener;

import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationListener;

/**
 * 应用启动失败事件监听器：承接 {@code ApplicationFailedEvent} 并进入异常处理流程。
 *
 * @author ddd4j
 * @since 4.0.x
 */
public class ApplicationFailedEventListener implements ApplicationListener<ApplicationFailedEvent> {

    /**
     * 无参构造，保持 Spring Bean 默认实例化语义。
     */
    public ApplicationFailedEventListener() {
    }

    /**
     * 启动失败后取出异常并交给异常处理方法。
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
