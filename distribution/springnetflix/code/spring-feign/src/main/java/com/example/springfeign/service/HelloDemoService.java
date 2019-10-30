package com.example.springfeign.service;

import com.example.springfeign.fallback.HelloDemoFallback;
import feign.Body;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient(name = "server-demo1", fallback = HelloDemoFallback.class)
public interface HelloDemoService {

    @RequestMapping(value = "", method = RequestMethod.GET)
    String index();

    @RequestMapping(value = "user", method = RequestMethod.GET)
    String getName(@RequestParam("name") String name);

    @Body("%7B\"name\":\"{name}\"%7D")
    @RequestMapping(value = "/test-user", method = RequestMethod.POST)
    String testName(@Param("name") String name);
}
