package com.cartigo.product.client;

import com.cartigo.product.dto.SellerDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.cartigo.product.client.fallback.SellerClientFallback;

@FeignClient(name = "USER-SERVICE",fallback = SellerClientFallback.class)
public interface SellerClient {

    @GetMapping("api/sellers/{user_id}")
    SellerDto getSellerById(@PathVariable Long user_id);
}
