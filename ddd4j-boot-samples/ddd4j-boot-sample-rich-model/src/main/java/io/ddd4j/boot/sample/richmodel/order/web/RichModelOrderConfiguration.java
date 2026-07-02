package io.ddd4j.boot.sample.richmodel.order.web;

import io.ddd4j.sample.richmodel.order.application.OrderApplicationService;
import io.ddd4j.sample.richmodel.order.domain.repository.OrderRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Wires the pure Java application service into Spring.
 */
@Configuration(proxyBeanMethods = false)
public class RichModelOrderConfiguration {

    @Bean
    public OrderApplicationService orderApplicationService(OrderRepository repository) {
        return new OrderApplicationService(repository);
    }
}
