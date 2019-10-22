package com.example.demo;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "com.girl")
public class GirProperties {

    private String name;

    private int height;

    private String face;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    @Override
    public String toString() {
        return "Girl{" +
                "name='" + name + '\'' +
                ", height=" + height +
                ", face='" + face + '\'' +
                '}';
    }
}
