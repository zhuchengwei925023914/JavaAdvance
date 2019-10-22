package com.example.springhystrix.controller;

import com.example.springhystrix.command.CustomerCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.GET;

@RestController
public class CustomerController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("index1")
    public Object getIndex() {
        return new CustomerCommand(restTemplate).execute();
    }

    // 注解配置方式
    @GetMapping("index2")
    @HystrixCommand(fallbackMethod = "callTimeoutFallback",
        threadPoolProperties = {
            @HystrixProperty(name = "coreSize", value = "1"),
                @HystrixProperty(name = "queueSizeRejectionThreshold", value = "1")
        },
        commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "100")
        })
    public Object getIndex2() {
        return restTemplate.getForObject("http://server-demo1", String.class, "");
    }

    public Object callTimeoutFallback() {
        return "降级拉";
    }
}
