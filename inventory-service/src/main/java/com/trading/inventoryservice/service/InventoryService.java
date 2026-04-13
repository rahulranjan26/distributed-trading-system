package com.trading.inventoryservice.service;

import com.trading.inventoryservice.dto.ProductRequestDto;
import com.trading.inventoryservice.dto.ProductResponseDto;
import com.trading.inventoryservice.entity.Product;
import com.trading.inventoryservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final ProductRepository productRepository;

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
        Pageable pageable = PageRequest.of(page,size);
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
}
