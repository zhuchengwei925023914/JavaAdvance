package com.example.springcloudzkconsumer.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient(name = "zk-provider")
public interface ConsumerClient {

    @GetMapping("user")
    String getUser(@RequestParam("name") String name);
}
