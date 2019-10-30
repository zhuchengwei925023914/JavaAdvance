package com.example.springfeign.fallback;

import com.example.springfeign.service.HelloDemoService;
import org.springframework.stereotype.Component;

@Component
public class HelloDemoFallback implements HelloDemoService {

    @Override
    public String index() {
        return "降级啦";
    }

    @Override
    public String getName(String name) {
        return "超时啦";
    }

    @Override
    public String testName(String name) {
        return "超时....";
    }
}
