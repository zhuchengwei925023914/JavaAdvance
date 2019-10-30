package com.example.springcloudstream.stream;

import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class SinkDemo {

    @StreamListener(Sink.INPUT)
    public void processVote(Message message) {
        System.out.println("收到数据: " + message.getPayload());
    }
}
