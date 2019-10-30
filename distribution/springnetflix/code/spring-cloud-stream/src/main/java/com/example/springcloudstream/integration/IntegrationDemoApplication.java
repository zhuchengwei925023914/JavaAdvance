package com.example.springcloudstream.integration;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.amqp.inbound.AmqpInboundChannelAdapter;
import org.springframework.integration.amqp.outbound.AmqpOutboundEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;

@SpringBootApplication
public class IntegrationDemoApplication {

    public static void main(String[] args) throws InterruptedException {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(IntegrationDemoApplication.class, args);
        Thread.sleep(5000);
        MessageChannel outBoundChannel = (MessageChannel) applicationContext.getBean("amqpOutChannel");
        MessagingTemplate messagingTemplate = new MessagingTemplate();
        messagingTemplate.sendAndReceive(outBoundChannel, new GenericMessage<Object>("我来自outBoundChannel"));
    }

    @Bean
    public MessageChannel amqpInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public AmqpInboundChannelAdapter inboundChannelAdapter(ConnectionFactory factory, @Qualifier("amqpInputChannel") MessageChannel messageChannel) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(factory);
        container.setQueueNames("queue-1");
        AmqpInboundChannelAdapter adapter = new AmqpInboundChannelAdapter(container);
        adapter.setOutputChannel(messageChannel);
        return adapter;
    }

    @Bean
    public MessageChannel amqpOutChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "amqpOutChannel")
    public AmqpOutboundEndpoint amqpOutbound(AmqpTemplate amqpTemplate) {
        AmqpOutboundEndpoint outboundEndpoint = new AmqpOutboundEndpoint(amqpTemplate);
        outboundEndpoint.setRoutingKey("queue-1");
        return outboundEndpoint;
    }
}
