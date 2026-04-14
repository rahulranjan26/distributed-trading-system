package com.trading.inventoryservice.controller;

import com.trading.inventoryservice.dto.ProductRequestDto;
import com.trading.inventoryservice.dto.ProductResponseDto;
import com.trading.inventoryservice.dto.PurchaseProductRequestDto;
import com.trading.inventoryservice.dto.PurchaseProductResponseDto;
import com.trading.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(path="/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping(path="/addProduct")
    public ResponseEntity<ProductResponseDto> addProduct(@RequestBody ProductRequestDto productRequestDto){
        return inventoryService.addProduct(productRequestDto);
    }
    @GetMapping(path = "/getProduct/{productId}")
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable Long productId) throws Exception {
        return inventoryService.getProduct(productId);
    }

    @GetMapping(path="/getProduct")
    public ResponseEntity<Page<ProductResponseDto>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return inventoryService.getAllProducts(page,size);
    }

    @PostMapping("/purchase")
    public ResponseEntity<PurchaseProductResponseDto> purchaseProduct(
            @RequestBody PurchaseProductRequestDto requestDto) throws Exception {
        return inventoryService.purchaseProduct(requestDto);
    }
}
