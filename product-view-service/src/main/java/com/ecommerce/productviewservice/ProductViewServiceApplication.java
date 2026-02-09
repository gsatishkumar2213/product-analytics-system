package com.ecommerce.productviewservice;

import com.ecommerce.shared.entity.ProductView;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan(basePackageClasses = ProductView.class)
@EnableJpaRepositories(basePackages = "com.ecommerce.productviewservice.repo")
@ComponentScan(basePackages = "com.ecommerce.productviewservice")
@SpringBootApplication
public class ProductViewServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductViewServiceApplication.class, args);
    }

}