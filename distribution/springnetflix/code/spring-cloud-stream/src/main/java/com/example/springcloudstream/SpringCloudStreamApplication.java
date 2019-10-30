package com.example.springcloudstream;

import com.example.springcloudstream.event.MyEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.bus.BusProperties;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class SpringCloudStreamApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(SpringCloudStreamApplication.class, args);
		BusProperties busProperties = context.getBean(BusProperties.class);
		context.publishEvent(new MyEvent("spring-cloud-bus", busProperties.getId()));
//		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
//		applicationContext.register(EventConfiguration.class);
//		applicationContext.refresh();
//		ApplicationEventPublisher publisher = applicationContext;
//		publisher.publishEvent(new MyApplicationEvent("spring event"));
	}

	public static class MyApplicationEvent extends ApplicationEvent {

		public MyApplicationEvent(String source) {
			super(source);
		}
	}

	@Configuration
	public static class EventConfiguration {

		@EventListener
		public void onEvent(MyApplicationEvent event) {
			System.out.println("收到事件: " + event);
		}
	}
}
