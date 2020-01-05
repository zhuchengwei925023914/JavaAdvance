package com.example.springcloudconsulconsumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
@RestController
public class SpringCloudConsulConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudConsulConsumerApplication.class, args);
	}

	@Autowired
	private RestTemplate restTemplate;

	@GetMapping("get-user")
	public String getUser(@RequestParam String name) {
		return restTemplate.getForObject("http://consul-provider/user?name=" + name, String.class);
	}

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
