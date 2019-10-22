package com.example.serverdemo2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableEurekaClient
@RestController
public class ServerDemo2Application {

	public static void main(String[] args) {
		SpringApplication.run(ServerDemo2Application.class, args);
	}

	@GetMapping("")
	public Object index() {
		String string = "这是server2";
		return string;
	}
}
