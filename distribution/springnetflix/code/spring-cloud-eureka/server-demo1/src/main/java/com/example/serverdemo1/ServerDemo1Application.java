package com.example.serverdemo1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Random;

@SpringBootApplication
@EnableEurekaClient
@RestController
public class ServerDemo1Application {

    public static void main(String[] args) {
        SpringApplication.run(ServerDemo1Application.class, args);
    }

    @GetMapping("")
    public Object index() {
        Random random = new Random();
        int time = random.nextInt(150);
        System.out.println("随机睡眠: " + time + " ms");
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String string = "这是server1";
        return string;
    }

    @GetMapping("user")
    public Object user(@RequestParam("name") String name) {
        return name;
    }

    @PostMapping("test-user")
    public Object testUser(@RequestBody Map<String, Object> map) {
        return map.get("name");
    }
}
