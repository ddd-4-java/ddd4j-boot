package io.ddd4j.boot.qrcode;

import io.ddd4j.extension.qrcode.resource.QrCodeResourceResolver;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

/** Resolves classpath and file resources through Spring's resource abstraction. */
public class SpringResourceQrCodeResolver implements QrCodeResourceResolver {

    private final ResourceLoader resourceLoader;

    public SpringResourceQrCodeResolver(ResourceLoader resourceLoader) {
        this.resourceLoader = Objects.requireNonNull(resourceLoader, "resourceLoader must not be null");
    }

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
