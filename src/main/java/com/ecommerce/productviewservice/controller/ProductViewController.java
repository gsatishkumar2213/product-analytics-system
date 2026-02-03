package com.ecommerce.productviewservice.controller;

import com.ecommerce.productviewservice.dto.ProductViewDTO;
import com.ecommerce.productviewservice.entity.ProductView;
import com.ecommerce.productviewservice.service.ProductViewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product-view")
public class ProductViewController {
    private Logger log = LoggerFactory.getLogger(ProductViewController.class);
    private ProductViewService productViewService;

    public ProductViewController(ProductViewService productViewService) {
        this.productViewService = productViewService;
    }

    @PostMapping
    public ResponseEntity<ProductView> createProductView(@RequestBody ProductViewDTO productViewDTO) {
        log.info("ProductViewDTO Request has been received: {}", productViewDTO);
        ProductView productView = productViewService.createProductView(productViewDTO);
        log.info("ProductView Request has been processed {}:", productView.toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(productView);
    }
}
