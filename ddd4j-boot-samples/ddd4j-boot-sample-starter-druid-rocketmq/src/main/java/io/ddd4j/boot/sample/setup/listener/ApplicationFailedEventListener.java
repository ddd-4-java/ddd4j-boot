package io.ddd4j.boot.sample.setup.listener;

import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationListener;

/**
 * 应用启动失败事件监听器。
 *
 * <p>监听 {@code ApplicationFailedEvent}，在 Spring Boot 启动失败时取出异常并交给统一处理方法，
 * 示例中该处理方法预留为空实现，可按需扩展告警或日志落盘逻辑。</p>
 */
public class ApplicationFailedEventListener implements ApplicationListener<ApplicationFailedEvent> {

    /**
     * 无参构造，保持 Spring 监听器默认实例化语义。
     */
    public ApplicationFailedEventListener() {
    }

    /**
     * 启动失败事件回调：提取启动异常并交由 {@link #handleThrowable(Throwable)} 处理。
     *
     * @param event 应用启动失败事件，携带启动过程中抛出的异常
     */
    @Override
    public void onApplicationEvent(ApplicationFailedEvent event) {
        Throwable throwable = event.getException();
        handleThrowable(throwable);
    }

    /**
     * 统一处理启动异常（示例为空实现，预留扩展点）。
     *
     * @param throwable 启动过程中抛出的异常
     */
    private void handleThrowable(Throwable throwable) {
    }

}
