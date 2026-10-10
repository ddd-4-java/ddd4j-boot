package io.ddd4j.boot.sample.setup.listener;

import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationListener;

/**
 * Spring Boot 启动失败事件监听器。
 * <p>
 * 监听 {@code ApplicationFailedEvent}，取出启动过程中抛出的异常并交给统一的异常处理逻辑，
 * 当前实现预留了异常扩展点，未做具体处理。
 * </p>
 */
public class ApplicationFailedEventListener implements ApplicationListener<ApplicationFailedEvent> {

    /**
     * 构造启动失败事件监听器（显式无参构造器，与编译器生成的默认构造器等价）。
     */
    public ApplicationFailedEventListener() {
    }

    /**
     * 启动失败事件回调：提取异常并交由内部处理方法处理。
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
