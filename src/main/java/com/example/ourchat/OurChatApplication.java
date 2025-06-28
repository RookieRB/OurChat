package com.example.ourchat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.ourchat.repository")
public class OurChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(OurChatApplication.class, args);
    }

}
