package io.hiwepy.boot.sample;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.micrometer.metrics.autoconfigure.MeterRegistryCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCaching(proxyTargetClass = true)
@EnableScheduling
@SpringBootApplication
public class MqttClientApplication implements CommandLineRunner {

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> configurer(
            @Value("${spring.application.name}") String applicationName) {
        return (registry) -> registry.config().commonTags("application", applicationName);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(MqttClientApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.err.println("Spring Boot Application（Mica-Mqtt-Client） Started !");
    }

}
