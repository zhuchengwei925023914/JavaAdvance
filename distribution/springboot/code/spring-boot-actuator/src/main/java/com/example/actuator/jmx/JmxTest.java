package com.example.actuator.jmx;

import org.springframework.stereotype.Component;

@Component
public class JmxTest implements JmxTestMBean {

    private String name;

    public String getName() {
        return null;
    }

    public void setName() {

    }

    public String printHello() {
        return "JmxTest "+ name;
    }

    public String printHello(String whoName) {
        return "JmxTest "+ whoName;
    }
}
