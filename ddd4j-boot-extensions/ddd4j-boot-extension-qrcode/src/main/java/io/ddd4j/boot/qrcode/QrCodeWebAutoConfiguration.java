package io.ddd4j.boot.qrcode;

import io.ddd4j.extension.qrcode.QrCodeService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;

/**
 * 按需开启的二维码 Servlet HTTP 端点装配类（渲染与解码）。
 *
 * <p>仅在 Servlet Web 应用且 {@code ddd4j.qrcode.web.enabled=true} 时生效。
 */
@AutoConfiguration(after = Ddd4jQrCodeBootAutoConfiguration.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(RestController.class)
@ConditionalOnProperty(prefix = QrCodeProperties.PREFIX + ".web", name = "enabled", havingValue = "true")
public class QrCodeWebAutoConfiguration {

    /**
     * 显式无参构造器，供 Spring 以自动装配方式实例化本配置类。
     */
    public QrCodeWebAutoConfiguration() {
    }

    /**
     * 注册二维码端点控制器。
     *
     * @param service    二维码服务
     * @param properties 二维码配置属性
     * @return {@link QrCodeController} 实例
     */
    @Bean
    public QrCodeController qrCodeController(QrCodeService service, QrCodeProperties properties) {
        return new QrCodeController(service, properties);
    }

    /**
     * 注册二维码异常处理建议（仅作用于二维码控制器）。
     *
     * @return {@link QrCodeExceptionHandler} 实例
     */
    @Bean
    public QrCodeExceptionHandler qrCodeExceptionHandler() {
        return new QrCodeExceptionHandler();
    }
}
