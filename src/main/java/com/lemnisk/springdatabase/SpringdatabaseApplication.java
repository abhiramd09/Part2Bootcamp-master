package com.lemnisk.springdatabase;

import org.apache.log4j.PropertyConfigurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringdatabaseApplication {

	public static void main(String[] args) {
		PropertyConfigurator.configure("/home/abhiram/Downloads/microservices/src/main/resources/log4j.properties");
		SpringApplication.run(SpringdatabaseApplication.class, args);
	}

}
