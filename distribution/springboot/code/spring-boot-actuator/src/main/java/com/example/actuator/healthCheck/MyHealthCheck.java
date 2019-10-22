package com.example.actuator.healthCheck;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class MyHealthCheck extends AbstractHealthIndicator {

    protected void doHealthCheck(Health.Builder builder) throws Exception {
        int i = new Random().nextInt();
        if (i % 2 == 0) {
            builder.withDetail("细节", "1").up();
        } else {
            builder.withDetail("细节", "2").down();
        }
    }
}
