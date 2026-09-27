package com.example.cachestampede.service;

import com.example.cachestampede.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    @Cacheable(value = "flash-sale", key = "#id", sync = true)
    public Product getProductById(Long id) {
        log.info("Fetching from Database for product {}", id);
        try {
            // Giả lập thời gian truy vấn DB phức tạp khoảng 2 giây
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Query interrupted", e);
        }
        return new Product(id, "Flash Sale Sneaker Pro", 199.99);
    }
}