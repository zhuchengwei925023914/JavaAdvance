package com.example.springcloudstream.event;

import org.springframework.cloud.bus.event.RemoteApplicationEvent;

public class MyEvent extends RemoteApplicationEvent {

    public MyEvent() {

    }

    public MyEvent(Object source, String originService) {
        super(source, originService, "**");
    }
}
