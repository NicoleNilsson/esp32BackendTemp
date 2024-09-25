package com.example.esp32backendtemp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Esp32BackendTempApplication {

	public static void main(String[] args) {
		SpringApplication.run(Esp32BackendTempApplication.class, args);
	}

}
