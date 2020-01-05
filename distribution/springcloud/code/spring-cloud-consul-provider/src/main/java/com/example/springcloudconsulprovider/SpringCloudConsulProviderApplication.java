package com.example.springcloudconsulprovider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableDiscoveryClient
@RestController
@RefreshScope
public class SpringCloudConsulProviderApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudConsulProviderApplication.class, args);
	}

	@Value("${test-key}")
	private String testKey;

	@GetMapping("user")
	public String getName(@RequestParam("name") String name) {
		System.out.println(testKey);
		return testKey;
	}
}
