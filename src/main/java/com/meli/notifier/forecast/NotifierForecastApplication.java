package com.meli.notifier.forecast;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class NotifierForecastApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotifierForecastApplication.class, args);
	}

}
