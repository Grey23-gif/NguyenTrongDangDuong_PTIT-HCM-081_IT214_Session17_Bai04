package com.example.cachestampede;

import com.example.cachestampede.model.Product;
import com.example.cachestampede.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class CacheStampedeApplication implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(CacheStampedeApplication.class);

    @Autowired
    private ProductService productService;

    public static void main(String[] args) {
        SpringApplication.run(CacheStampedeApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("===============================================================");
        log.info("BẮT ĐẦU THỬ NGHIỆM ĐỒNG THỜI (50 REQUESTS) ĐẾN CÙNG ID SẢN PHẨM");
        log.info("===============================================================");

        int totalRequests = 50;
        Long targetProductId = 1001L;
        ExecutorService executor = Executors.newFixedThreadPool(totalRequests);
        List<CompletableFuture<Product>> futures = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < totalRequests; i++) {
            final int reqNo = i + 1;
            futures.add(CompletableFuture.supplyAsync(() -> {
                log.debug("Request #{} bắt đầu gửi...", reqNo);
                return productService.getProductById(targetProductId);
            }, executor));
        }

        // Chờ tất cả 50 luồng hoàn tất
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        log.info("===============================================================");
        log.info("KẾT QUẢ THỬ NGHIỆM:");
        log.info("- Tổng số request: {}", totalRequests);
        log.info("- Tổng thời gian thực thi: {} ms", duration);
        log.info("- Thời gian kỳ vọng: ~2000 ms (Chỉ chờ 1 lần query DB)");
        log.info("===============================================================");

        executor.shutdown();
    }
}