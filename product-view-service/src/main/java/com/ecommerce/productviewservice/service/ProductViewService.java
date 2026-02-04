package com.ecommerce.productviewservice.service;

import com.ecommerce.productviewservice.dto.ProductViewDTO;
import com.ecommerce.productviewservice.kafka.ProductViewProducer;
import com.ecommerce.productviewservice.repo.ProductViewRepository;
import com.ecommerce.shared.entity.ProductView;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
public class ProductViewService {
    private ProductViewRepository productViewRepository;
    private ObjectMapper objectMapper;
    private ProductViewProducer productViewProducer;

    public ProductViewService(ProductViewRepository productViewRepository,
                              ObjectMapper objectMapper, ProductViewProducer productViewProducer) {
        this.productViewRepository = productViewRepository;
        this.objectMapper = objectMapper;
        this.productViewProducer = productViewProducer;
    }

    public ProductView createProductView(ProductViewDTO productViewDTO) {
        ProductView productView =
                objectMapper.convertValue(productViewDTO, ProductView.class);
        productView.setViewedAt(LocalDateTime.now());
        ProductView response = productViewRepository.save(productView);
        CompletableFuture<SendResult<String, ProductView>> producedMessage =
                productViewProducer.produceProductView(response);

        return response;
    }

}
