package com.devashish94.video_catalog_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class VideoCatalogServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(VideoCatalogServiceApplication.class, args);
	}

}
