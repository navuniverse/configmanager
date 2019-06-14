package com.rockingengineering.configmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class ConfigManagerApp extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(ConfigManagerApp.class, args);
	}
}