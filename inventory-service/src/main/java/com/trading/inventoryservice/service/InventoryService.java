package com.trading.inventoryservice.service;

import com.trading.inventoryservice.dto.external.InventoryRequest;
import com.trading.inventoryservice.dto.external.InventoryResponse;
import com.trading.inventoryservice.dto.internal.ProductRequestDto;
import com.trading.inventoryservice.dto.internal.ProductResponseDto;
import com.trading.inventoryservice.dto.internal.PurchaseProductRequestDto;
import com.trading.inventoryservice.dto.internal.PurchaseProductResponseDto;
import com.trading.inventoryservice.entity.Product;
import com.trading.inventoryservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final ProductRepository productRepository;
    private final ReentrantLock reentrantLock = new ReentrantLock();
    private final RedissonClient redissonClient;

    public ResponseEntity<ProductResponseDto> addProduct(ProductRequestDto productRequestDto) {
        log.info("Adding new Product with productName: {}", productRequestDto.getProductName());
        Product newProduct = Product.builder()
                .productName(productRequestDto.getProductName())
                .price(productRequestDto.getPrice())
                .description(productRequestDto.getDescription())
                .quantity(productRequestDto.getQuantity())
                .build();

        Product savedProduct = productRepository.save(newProduct);
        ProductResponseDto finalProduct = ProductResponseDto.builder()
                .productName(savedProduct.getProductName())
                .productId(savedProduct.getProductId())
                .price(savedProduct.getPrice())
                .quantity(savedProduct.getQuantity())
                .description(savedProduct.getDescription())
                .createdAt(savedProduct.getCreatedAt())
                .updatedAt(savedProduct.getUpdatedAt())
                .build();

        return ResponseEntity.ok(finalProduct);
    }

    public ResponseEntity<ProductResponseDto> getProduct(Long productId) throws Exception {
        log.info("Getting product with Product Id:{}", productId);
        Optional<Product> savedProduct = productRepository.findById(productId);

        if (savedProduct.isEmpty())
            throw new Exception("product not found with product id:{}");
        Product product = savedProduct.get();
        ProductResponseDto responseDto = ProductResponseDto.builder()
                .productName(product.getProductName())
                .productId(productId)
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .description(product.getDescription())
                .build();

        return ResponseEntity.ok(responseDto);

    }

    public ResponseEntity<Page<ProductResponseDto>> getAllProducts(int page, int size) {
        log.info("Getting all the products of inventory");
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findAll(pageable);

        Page<ProductResponseDto> finalProducts = products.map(
                p -> {
                    ProductResponseDto pd = ProductResponseDto.builder()
                            .productName(p.getProductName())
                            .productId(p.getProductId())
                            .price(p.getPrice())
                            .quantity(p.getQuantity())
                            .createdAt(p.getCreatedAt())
                            .updatedAt(p.getUpdatedAt())
                            .description(p.getDescription())
                            .build();
                    return pd;
                }
        );
        return ResponseEntity.ok(finalProducts);
    }


    /**
     * Phase 1 :  Using JVM lock but since the JVM lock is single instance lock, we need to update this in multi instance scenario
     * <p>
     * Phase 2 : We use Redisson Lock which is multi instance lock and has a shared lock which is shared among different instances
     *
     */

    public ResponseEntity<PurchaseProductResponseDto> purchaseProduct(PurchaseProductRequestDto purchaseProductRequestDto) throws Exception {
        log.info("We are purchase the product with product id:{}", purchaseProductRequestDto.getProductId());

        /// Phase 1

//        reentrantLock.lock();
//        try {
//            Optional<Product> product = productRepository.findById(purchaseProductRequestDto.getProductId());
//            if (product.isEmpty())
//                throw new Exception("Product is not present");
//
//            Product existentProduct = product.get();
//            if (existentProduct.getQuantity() == 0 || existentProduct.getQuantity() < purchaseProductRequestDto.getQuantity())
//                throw new Exception("Insufficient product in the inventory");
//
//
//            double totalCost = purchaseProductRequestDto.getQuantity() * existentProduct.getPrice();
//
//            Long finalQuantity = existentProduct.getQuantity() - purchaseProductRequestDto.getQuantity();
//
//            existentProduct.setQuantity(finalQuantity);
//            Product savedProduct = productRepository.save(existentProduct);
//            PurchaseProductResponseDto responseDto = PurchaseProductResponseDto.builder()
//                    .cost(totalCost)
//                    .productId(existentProduct.getProductId())
//                    .quantity(finalQuantity)
//                    .build();
//
//            return ResponseEntity.ok(responseDto);
//        }finally {
//            reentrantLock.unlock();
//        }

        // Phase 2:
        RLock lock = redissonClient.getLock("lock:product" + purchaseProductRequestDto.getProductId());
        lock.lock();
        try {

            Optional<Product> product = productRepository.findById(purchaseProductRequestDto.getProductId());
            if (product.isEmpty())
                throw new Exception("Product is not present");

            Product existentProduct = product.get();
            if (existentProduct.getQuantity() == 0 || existentProduct.getQuantity() < purchaseProductRequestDto.getQuantity())
                throw new Exception("Insufficient product in the inventory");


            double totalCost = purchaseProductRequestDto.getQuantity() * existentProduct.getPrice();

            Long finalQuantity = existentProduct.getQuantity() - purchaseProductRequestDto.getQuantity();

            existentProduct.setQuantity(finalQuantity);
            Product savedProduct = productRepository.save(existentProduct);
            PurchaseProductResponseDto responseDto = PurchaseProductResponseDto.builder()
                    .cost(totalCost)
                    .productId(existentProduct.getProductId())
                    .quantity(finalQuantity)
                    .build();

            return ResponseEntity.ok(responseDto);
        } finally {
            lock.unlock();
        }
    }

    public List<InventoryResponse> checkProduct(List<InventoryRequest> request) {
        log.info("Checking Product Availability");

        List<InventoryResponse> response = new ArrayList<>();
        for (var res : request) {
            RLock lock = redissonClient.getLock("inventory:" + res.getProductId());
            lock.lock();
            try {
                Optional<Product> dbProduct = productRepository.findById(res.getProductId());
                if (dbProduct.isEmpty()) {
                    log.info("The product with Id: {} Not present in inventory", res.getProductId());
                    continue;
                }
                Product product = dbProduct.get();
                if (product.getQuantity() < res.getQuantity()) {
                    log.info("Not enough quantity of Product is present for product with Id : {}", res.getProductId());
                    continue;
                }

                InventoryResponse newInventoryResponse = InventoryResponse.builder()
                        .available(true)
                        .price(BigDecimal.valueOf(product.getPrice()))
                        .productId(product.getProductId())
                        .build();

                response.add(newInventoryResponse);
                product.setQuantity(product.getQuantity() - res.getQuantity());
                productRepository.save(product);

            } finally {
                lock.unlock();
            }


        }
        return response;
    }
}