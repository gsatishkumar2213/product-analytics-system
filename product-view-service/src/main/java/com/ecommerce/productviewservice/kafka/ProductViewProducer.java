package com.ecommerce.productviewservice.kafka;


import com.ecommerce.shared.entity.ProductView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class ProductViewProducer {
    private KafkaTemplate<String, ProductView> kafkaTemplate;
    private Logger log = LoggerFactory.getLogger(ProductViewProducer.class);

    public ProductViewProducer(KafkaTemplate<String, ProductView> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public CompletableFuture<SendResult<String, ProductView>> produceProductView(ProductView productView) {
        log.info("ProductViewProducer is producing a message {}:", productView);
        CompletableFuture<SendResult<String, ProductView>> result = kafkaTemplate.send("product" +
                "-view", productView.getId().toString(), productView);
        result.thenAccept(sendResult -> {
            log.info("Successfully produced to product-view");
        }).exceptionally(ex -> {
            log.error("Failed to produce message", ex);
            return null;
        });
        return result;
    }
}
