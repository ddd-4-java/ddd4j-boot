package io.ddd4j.boot.observability;

import io.ddd4j.core.context.ThreadContext;
import org.slf4j.MDC;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;

/**
 * 追踪上下文透传执行器装饰器：提交任务时快照 MDC + TTL ThreadContext，
 * 任务执行期间恢复快照，结束后还原任务前状态，保证普通线程池也能透传追踪上下文。
 *
 * <p>底座 TTL 包装的线程池由 TransmittableThreadLocal 天然透传；
 * 本装饰器补齐普通 {@link Executor} 场景（对应家族 design D2/D3）。
 * 对应 spec：boot-trace-observability / Trace context propagation（异步线程池透传场景）。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
public final class TraceContextPropagatingExecutor implements Executor {

    /**
     * 被装饰的底层执行器。
     */
    private final Executor delegate;

    private TraceContextPropagatingExecutor(final Executor delegate) {
        this.delegate = Objects.requireNonNull(delegate, "delegate executor must not be null");
    }

    /**
     * 装饰任意执行器，使其透传 MDC + TTL ThreadContext 追踪上下文。
     *
     * @param delegate 底层执行器
     * @return 透传执行器
     */
    public static Executor decorate(final Executor delegate) {
        if (delegate instanceof TraceContextPropagatingExecutor) {
            return delegate;
        }
        return new TraceContextPropagatingExecutor(delegate);
    }

    /**
     * 提交任务：捕获提交方上下文快照，任务执行前后恢复/还原。
     *
     * @param command 任务
     */
    @Override
    public void execute(final Runnable command) {
        Snapshot submitterSnapshot = Snapshot.capture();
        delegate.execute(() -> {
            Snapshot previous = Snapshot.capture();
            submitterSnapshot.restore();
            try {
                command.run();
            } finally {
                previous.restore();
            }
        });
    }

    /**
     * 上下文快照：MDC 副本 + TTL ThreadContext 副本。
     */
    private static final class Snapshot {

        /**
         * MDC 副本（null 表示提交时为空）。
         */
        private final Map<String, String> mdc;

        /**
         * TTL ThreadContext 副本（null 表示提交时为空）。
         */
        private final Map<Object, Object> ttl;

        private Snapshot(final Map<String, String> mdc, final Map<Object, Object> ttl) {
            this.mdc = mdc;
            this.ttl = ttl;
        }

        /**
         * 捕获当前线程上下文快照。
         *
         * @return 快照
         */
        private static Snapshot capture() {
            return new Snapshot(MDC.getCopyOfContextMap(), ThreadContext.getValues());
        }

        /**
         * 将快照恢复到当前线程（空快照等价于清空）。
         */
        private void restore() {
            if (Objects.isNull(mdc)) {
                MDC.clear();
            } else {
                MDC.setContextMap(mdc);
            }
            if (Objects.isNull(ttl) || ttl.isEmpty()) {
                ThreadContext.clear();
            } else {
                ThreadContext.setValues(ttl);
            }
        }
    }
}
