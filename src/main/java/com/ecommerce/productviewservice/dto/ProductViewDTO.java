package com.ecommerce.productviewservice.dto;

import com.ecommerce.productviewservice.entity.DeviceType;

public record ProductViewDTO(Long productId,
                             String productName,
                             String category,
                             DeviceType deviceType,
                             String userId) {
}
