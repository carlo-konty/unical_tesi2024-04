package com.tesi.unical;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class ProgettoTesiApplication extends SpringBootServletInitializer {


	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(ProgettoTesiApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(ProgettoTesiApplication.class, args);
	}

}
