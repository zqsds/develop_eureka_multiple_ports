package com.example.hcy_bridge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ClientTestApplication {

  public static void main(String[] args) {
    SpringApplication.run(ClientTestApplication.class, args);
  }
}
