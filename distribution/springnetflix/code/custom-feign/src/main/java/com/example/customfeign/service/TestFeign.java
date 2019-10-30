package com.example.customfeign.service;

import com.example.customfeign.annotation.FeignClient;
import com.example.customfeign.annotation.FeignGet;

@FeignClient(baseUrl = "http://www.baidu.com:80")
public interface TestFeign {

    @FeignGet(url = "index.html")
    Object getSomeThing();
}
