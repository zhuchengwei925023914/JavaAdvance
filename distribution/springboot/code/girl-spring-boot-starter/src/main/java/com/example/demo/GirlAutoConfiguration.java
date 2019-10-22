package com.example.demo;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(GirProperties.class)
public class GirlAutoConfiguration {

    @Bean
    public Girl getGirl(GirProperties girProperties) {
        Girl girl = new Girl();
        girl.setName(girProperties.getName());
        girl.setHeight(girProperties.getHeight());
        girl.setFace(girProperties.getFace());
        return girl;
    }
}
