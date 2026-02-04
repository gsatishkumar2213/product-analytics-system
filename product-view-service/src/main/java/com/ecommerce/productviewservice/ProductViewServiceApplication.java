package com.ecommerce.productviewservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan(basePackages = {"com.ecommerce.shared.entity"})
@EnableJpaRepositories(basePackages = {"com.ecommerce.productviewservice.repo"})
@ComponentScan(basePackages = {"com.ecommerce.productviewservice"})
@SpringBootApplication
public class ProductViewServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductViewServiceApplication.class, args);
    }

}
