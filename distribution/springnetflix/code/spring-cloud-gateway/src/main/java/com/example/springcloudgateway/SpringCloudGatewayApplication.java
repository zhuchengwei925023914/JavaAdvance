package com.example.springcloudgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class SpringCloudGatewayApplication {

	public static void main(String[] args) {
		String test = new BCryptPasswordEncoder().encode("nacos");
		System.out.println(test);
		SpringApplication.run(SpringCloudGatewayApplication.class, args);
	}

}
