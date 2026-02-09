package com.ecommerce.productanalyticsservice.producer;

import com.ecommerce.shared.entity.Product;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ProductCatalogProducer {
    private final KafkaTemplate<String, Product> kafkaTemplate;

    public ProductCatalogProducer(KafkaTemplate<String, Product> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void produceProductCatalog(Product product) {
        kafkaTemplate.send("product-catalog", product.getProductId().toString(), product);
    }
}
