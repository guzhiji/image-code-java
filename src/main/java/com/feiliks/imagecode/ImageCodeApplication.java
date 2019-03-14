package com.feiliks.imagecode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class ImageCodeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ImageCodeApplication.class, args);
	}
}
