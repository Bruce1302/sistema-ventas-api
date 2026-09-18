package com.project.sistema_ventas_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SistemaVentasApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SistemaVentasApiApplication.class, args);
	}

}
