package com.trading.inventoryservice.dto.internal;


import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductRequestDto {
    private String productName;

    private String description;

    private double price;

    private Long quantity;

}
