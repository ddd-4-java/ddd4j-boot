package io.ddd4j.boot.qrcode;

import io.ddd4j.extension.qrcode.resource.QrCodeResourceResolver;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

/**
 * 通过 Spring 资源抽象解析 classpath 与文件资源的二维码资源解析器。
 */
public class SpringResourceQrCodeResolver implements QrCodeResourceResolver {

    private final ResourceLoader resourceLoader;

    /**
     * 以 Spring {@link ResourceLoader} 构造解析器。
     *
     * @param resourceLoader Spring 资源加载器，不允许为 {@code null}
     * @throws NullPointerException 参数为 {@code null} 时抛出
     */
    public SpringResourceQrCodeResolver(ResourceLoader resourceLoader) {
        this.resourceLoader = Objects.requireNonNull(resourceLoader, "resourceLoader must not be null");
    }

    /**
     * 解析资源位置为 {@link BufferedImage}（不支持的图像格式视为 IO 错误）。
     *
     * @param location 资源位置（如 {@code classpath:qr/logo.png}）
     * @return 解码后的图像
     * @throws IOException 资源读取失败或图像格式不受支持时抛出
     */
    @Override
    public BufferedImage resolve(String location) throws IOException {
        Resource resource = resourceLoader.getResource(location);
        try (java.io.InputStream inputStream = resource.getInputStream()) {
            BufferedImage image = ImageIO.read(inputStream);
            if (Objects.isNull(image)) {
                throw new IOException("Unsupported image resource: " + location);
            }
            return image;
        }
    }
}
