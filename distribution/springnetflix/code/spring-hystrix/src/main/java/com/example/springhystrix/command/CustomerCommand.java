package com.example.springhystrix.command;

import com.netflix.hystrix.*;
import org.springframework.web.client.RestTemplate;

public class CustomerCommand extends HystrixCommand<Object> {

    private RestTemplate restTemplate;

    public CustomerCommand(RestTemplate restTemplate) {
        super(
                Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("study-hystrix"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("CustomerController"))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("studyThreadPool"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                    .withExecutionTimeoutInMilliseconds(100)
                    .withCircuitBreakerSleepWindowInMilliseconds(5000))
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                    .withCoreSize(1)
                    .withMaxQueueSize(2))
        );
        this.restTemplate = restTemplate;
    }

    @Override
    protected Object run() throws Exception {
        // 调用我们想要调用的方法
        System.out.println("当前线程是: " + Thread.currentThread().getName());
        return restTemplate.getForObject("http://server-demo1", String.class, "");
    }

    @Override
    protected Object getFallback() {
        // 降级后调用此方法
        System.out.println("降级处理");
        return "降级处理";
    }
}
