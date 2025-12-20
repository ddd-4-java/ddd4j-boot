package io.ddd4j.boot.sample.setup.config;

import jakarta.jms.Queue;
import jakarta.jms.Topic;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActiveMQConfiguration {

    @Bean
    public Queue queue() {
        return new ActiveMQQueue("demo.queue");
    }

    @Bean
    public Topic topic() {
        return new ActiveMQTopic("demo.topic");
    }

}
