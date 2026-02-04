package com.ecommerce.productviewservice.dto;


import com.ecommerce.shared.entity.DeviceType;

public record ProductViewDTO(Long productId,
                             String productName,
                             String category,
                             DeviceType deviceType,
                             String userId) {
}
