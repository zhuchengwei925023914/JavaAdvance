package com.example.springcloudribbon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CustomerController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @GetMapping("index")
    public Object getIndex() {
        ServiceInstance serviceInstance = loadBalancerClient.choose("server-demo");
        String ip = serviceInstance.getHost();
        int port = serviceInstance.getPort();
        return restTemplate.getForObject("http://" + ip + ":" + port, String.class, "");

    }
}
