package com.example.customfeign.controller;

import com.example.customfeign.service.TestFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private TestFeign testFeign;

    @GetMapping("")
    public Object getIndex() {
        return testFeign.getSomeThing();
    }
}
