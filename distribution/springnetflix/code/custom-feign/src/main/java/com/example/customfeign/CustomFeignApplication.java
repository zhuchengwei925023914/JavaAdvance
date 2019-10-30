package com.example.customfeign;

import com.example.customfeign.util.FeignRegister;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(FeignRegister.class)
public class CustomFeignApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomFeignApplication.class, args);
	}

}
