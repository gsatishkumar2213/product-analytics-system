package com.ecommerce.productviewservice.repo;

import com.ecommerce.productviewservice.entity.DeviceType;
import com.ecommerce.productviewservice.entity.ProductView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductViewRepository extends JpaRepository<ProductView, Long> {
    List<ProductView> findByUserId(String userId);

    List<ProductView> findByCategory(String category);

    List<ProductView> findByDeviceType(DeviceType deviceType);

    List<ProductView> findByProductName(String productName);
}
