package com.cartigo.reporting.integration;

import com.cartigo.reporting.integration.dto.ReturnAgg;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "${reporting.sources.returns.name:cartigo-return-refund-service}")
public interface ReturnReportingClient {

    @GetMapping("/api/returns/reporting/daily")
    List<ReturnAgg> dailyAgg();
}
