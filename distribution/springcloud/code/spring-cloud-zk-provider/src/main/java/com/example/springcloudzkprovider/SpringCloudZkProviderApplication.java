package com.example.springcloudzkprovider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableDiscoveryClient
public class SpringCloudZkProviderApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudZkProviderApplication.class, args);
	}

	@GetMapping("user")
	public String getName(@RequestParam("name") String name) {
		return name;
	}
}
