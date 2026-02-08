package com.cartigo.reporting.integration;

import com.cartigo.reporting.integration.dto.OrderAgg;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "${reporting.sources.orders.name:cartigo-order-service}")
public interface OrderReportingClient {

    /**
     * Optional endpoint on order-service.
     * If you don't have it, set reporting.sources.orders.enabled=false
     */
    @GetMapping("/api/orders/reporting/daily")
    List<OrderAgg> dailyAgg();
}
