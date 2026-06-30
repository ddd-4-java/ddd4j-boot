package io.ddd4j.boot.mq.pulsar.config;

import io.ddd4j.boot.mq.pulsar.autoconfigure.Ddd4jPulsarMQAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import(Ddd4jPulsarMQAutoConfiguration.class)
public class PulsarMQBootAutoConfiguration {
}
