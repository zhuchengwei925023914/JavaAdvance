package com.example.actuator.endpoint;

import org.springframework.boot.actuate.endpoint.annotation.*;
import org.springframework.stereotype.Component;

@Endpoint(id = "myEndpoint")
@Component
public class MyEndpoint {

    String name = "default";

    @ReadOperation
    public String getName() {
        return "{\"name\":\"" + name + "\"}";
    }

    @DeleteOperation
    public void delName() {
        name = "";
    }

    @WriteOperation
    public void setName(@Selector String name) {
        this.name = name;
    }
}
