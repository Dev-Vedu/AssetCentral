package com.itasset.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AssetmanagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(AssetmanagementApplication.class, args);
	}

}