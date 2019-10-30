package com.example.springcloudstream.integration;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AppConfig {

    @Bean
    public Queue queue() {
        return new Queue("queue-1");
    }
}
