package com.ecommerce.productanalyticsservice.initializer;

import com.ecommerce.productanalyticsservice.producer.ProductCatalogProducer;
import com.ecommerce.shared.entity.Product;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Profile("dev")
@Component
public class DataInitializer {

    private final ProductCatalogProducer productCatalogProducer;

    public DataInitializer(ProductCatalogProducer productCatalogProducer) {
        this.productCatalogProducer = productCatalogProducer;
    }

    @PostConstruct
    public void initializeData() {
        log.info("Seeding sample product catalog data...");

        productCatalogProducer.produceProductCatalog(new Product(1L, "Wireless Mouse", 29.99));
        productCatalogProducer.produceProductCatalog(new Product(2L, "Mechanical Keyboard", 89.99));
        productCatalogProducer.produceProductCatalog(new Product(3L, "USB-C Cable", 12.99));
        productCatalogProducer.produceProductCatalog(new Product(4L, "Laptop Stand", 45.99));
        productCatalogProducer.produceProductCatalog(new Product(5L, "Webcam HD", 59.99));

        log.info("Successfully seeded {} products to catalog", 5);
    }
}