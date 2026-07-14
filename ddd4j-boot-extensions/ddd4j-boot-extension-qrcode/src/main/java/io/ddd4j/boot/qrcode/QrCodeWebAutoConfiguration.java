package io.ddd4j.boot.qrcode;

import io.ddd4j.extension.qrcode.QrCodeService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;

/** Opt-in servlet HTTP endpoints for QR code rendering and decoding. */
@AutoConfiguration(after = Ddd4jQrCodeBootAutoConfiguration.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(RestController.class)
@ConditionalOnProperty(prefix = QrCodeProperties.PREFIX + ".web", name = "enabled", havingValue = "true")
public class QrCodeWebAutoConfiguration {

    @Bean
    public QrCodeController qrCodeController(QrCodeService service, QrCodeProperties properties) {
        return new QrCodeController(service, properties);
    }

    @Bean
    public QrCodeExceptionHandler qrCodeExceptionHandler() {
        return new QrCodeExceptionHandler();
    }
}
