package com.jpabook.jpashop;

import org.springframework.boot.SpringApplication;

public class TestJpashopApplication {

	public static void main(String[] args) {
		SpringApplication.from(JpashopApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
