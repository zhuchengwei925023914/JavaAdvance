package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class SpringBootDemoApplication {

	@Autowired
	private Girl girl;

	@RequestMapping("user")
	public String hello(String id) {
		return girl.toString();
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringBootDemoApplication.class, args);
	}
}
