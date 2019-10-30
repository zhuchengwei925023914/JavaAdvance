package com.example.springcloudstream.integration;

import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class DataHandler {

    @ServiceActivator(inputChannel = "amqpInputChannel")
    public void printMessage(Message<?> message) {
        System.out.println("收到消息: " + message.getPayload().toString());
    }
}
