package io.ddd4j.boot.web.webflux;

import io.ddd4j.annotation.WebSocketMapping;
import io.ddd4j.core.exception.BizRuntimeException;
import org.springframework.beans.BeansException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * WebSocket 映射处理器：自动注册带 {@link WebSocketMapping} 注解的 WebSocketHandler。
 *
 * @author ddd4j
 * @since 4.0.x
 * @see <a href="https://blog.csdn.net/qq_18537055/article/details/98681154">参考文档</a>
 */
public class WebSocketMappingHandlerMapping extends SimpleUrlHandlerMapping {

    private final Map<String, WebSocketHandler> handlerMap = new LinkedHashMap<>();

    @Override
    public void initApplicationContext() throws BeansException {
        Map<String, Object> beanMap = obtainApplicationContext().getBeansWithAnnotation(WebSocketMapping.class);
        beanMap.values().forEach(bean -> {
            if (!(bean instanceof WebSocketHandler)) {
                throw new BizRuntimeException(
                        String.format("Controller [%s] doesn't implement WebSocketHandler interface.",
                                bean.getClass().getName()));
            }
            WebSocketMapping annotation = AnnotationUtils.getAnnotation(
                    bean.getClass(), WebSocketMapping.class);
            handlerMap.put(Objects.requireNonNull(annotation).value(), (WebSocketHandler) bean);
        });
        super.setOrder(Ordered.HIGHEST_PRECEDENCE);
        super.setUrlMap(handlerMap);
        super.initApplicationContext();
    }

}
