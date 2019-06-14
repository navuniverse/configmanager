package com.rockingengineering.configmanager.config;

import java.util.ArrayList;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.rockingengineering.configmanager.controller"))
				.build()
				.apiInfo(apiInfo());
	}

	private ApiInfo apiInfo() {
		Contact contact = new Contact("Naveen Kumar", "www.rockingengineering.com", "contactrockingengineering@gmail.com");

		return new ApiInfo(
				"Config Manager Micro Service",
				"AWS Parameter Store Config Manager Service",
				"V1.0.0",
				"https://www.rockingengineering.com/terms-conditions",
				contact,
				"RockingEngineering Internal License",
				"https://www.rockingengineering.com/privacy-policy",
				new ArrayList<>());
	}

}