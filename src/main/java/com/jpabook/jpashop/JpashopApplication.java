package com.jpabook.jpashop;

import com.atomikos.remoting.spring.rest.TransactionAwareRestContainerFilter;
import com.atomikos.remoting.twopc.AtomikosRestPort;
import com.atomikos.remoting.twopc.ParticipantsProvider;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JpashopApplication {

	public static void main(String[] args) {
		System.setProperty("com.atomikos.icatch.log_base_name", "jpashop-seoul");
		System.setProperty("com.atomikos.icatch.rest_port_url", "http://localhost:8091");
		SpringApplication.run(JpashopApplication.class, args);
	}
}
