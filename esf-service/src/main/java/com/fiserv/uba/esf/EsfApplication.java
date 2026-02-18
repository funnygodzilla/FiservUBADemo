package com.fiserv.uba.esf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class EsfApplication {
    public static void main(String[] args) { SpringApplication.run(EsfApplication.class, args); }
}
