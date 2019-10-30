package com.example.springfeign.controller;

import com.example.springfeign.service.HelloDemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {

    @Autowired
    private HelloDemoService helloDemoService;

    @GetMapping("index")
    public Object getIndex() {
        return helloDemoService.index();
    }

    @GetMapping("find-user")
    public Object getUser(@RequestParam("name") String name) {
        return helloDemoService.getName(name);
    }

    @GetMapping("/set-user")
    public Object setUser(@RequestParam("name") String name) {
        return helloDemoService.testName(name);
    }
}
