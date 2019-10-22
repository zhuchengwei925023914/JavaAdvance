package com.example.actuator.jmx;

public interface JmxTestMBean {
    String getName();
    void setName();
    String printHello();
    String printHello(String whoName);
}
