package com.trading.orderservice.clients;


import com.trading.orderservice.dto.external.InventoryRequest;
import com.trading.orderservice.dto.external.InventoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name="inventory-service")
public interface InventoryClient {

    @PostMapping(path = "/inventory/check")
    List<InventoryResponse> checkProduct(@RequestBody List<InventoryRequest> request);
}


