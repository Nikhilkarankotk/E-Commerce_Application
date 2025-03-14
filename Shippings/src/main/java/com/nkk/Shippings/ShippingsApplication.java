package com.nkk.Shippings;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ShippingsApplication {
	public static void main(String[] args) {
		SpringApplication.run(ShippingsApplication.class, args);
	}

}
