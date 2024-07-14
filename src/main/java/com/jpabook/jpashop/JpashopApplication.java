package com.jpabook.jpashop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JpashopApplication {

	public static void main(String[] args) {
		System.setProperty("com.atomikos.icatch.log_base_name", "jpashop-seoul");
		System.setProperty("com.atomikos.icatch.rest_port_url", "http://localhost:8091/atomikos/");
		SpringApplication.run(JpashopApplication.class, args);
	}
}