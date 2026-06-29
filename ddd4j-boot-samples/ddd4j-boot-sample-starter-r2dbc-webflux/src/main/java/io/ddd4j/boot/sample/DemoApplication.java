package io.ddd4j.boot.sample;

import io.ddd4j.boot.sample.entity.MyEvent;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> configurer(
            @Value("${spring.application.name}") String applicationName) {
        return (registry) -> registry.config().commonTags("application", applicationName);
    }

    @Override
    public void run(String... args) throws Exception {
        System.err.println("Spring Boot Application（ddd4j-boot-Demo） Started !");
    }

    @Bean
    public CommandLineRunner initData(MongoOperations mongo) {  // 2
        return (String... args) -> {    // 3
            mongo.dropCollection(MyEvent.class);    // 4
            mongo.createCollection(MyEvent.class, CollectionOptions.empty().size(200).capped()); // 5
        };
    }

}
