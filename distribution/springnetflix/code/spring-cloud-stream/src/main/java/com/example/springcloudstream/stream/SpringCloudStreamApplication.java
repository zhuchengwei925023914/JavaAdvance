package com.example.springcloudstream.stream;

import com.example.springcloudstream.stream.convert.MyConvert;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamMessageConverter;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.core.MessageSource;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.support.GenericMessage;

@SpringBootApplication
@EnableBinding(value = {Sink.class, Source.class})
public class SpringCloudStreamApplication {

    public static void main(String[] args) throws InterruptedException {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(SpringCloudStreamApplication.class, args);
        Source source = applicationContext.getBean(Source.class);
        Thread.sleep(3000L);
        source.output().send(new GenericMessage("12345"));
    }

    // 和integration集成用法，循环发送
    @Bean
    @InboundChannelAdapter(value = Source.OUTPUT, poller = @Poller(fixedDelay = "5000", maxMessagesPerPoll = "1"))
    public MessageSource<String> sendMessage() {
        return new MessageSource<String>() {
            @Nullable
            @Override
            public Message<String> receive() {
                return new GenericMessage("hello, spring could stream");
            }
        };
    }

    @Bean
    @StreamMessageConverter
    public MessageConverter messageConverter() {
        return new MyConvert();
    }
}
