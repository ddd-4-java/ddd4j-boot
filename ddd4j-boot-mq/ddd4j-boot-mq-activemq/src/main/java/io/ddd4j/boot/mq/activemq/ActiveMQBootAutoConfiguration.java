package io.ddd4j.boot.mq.activemq;

import io.ddd4j.boot.mq.activemq.autoconfigure.Ddd4jActiveMQAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import(Ddd4jActiveMQAutoConfiguration.class)
public class ActiveMQBootAutoConfiguration {
}
