package com.cartigo.reporting.integration;

import com.cartigo.reporting.integration.dto.ReviewAgg;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "${reporting.sources.reviews.name:cartigo-review-service}")
public interface ReviewReportingClient {

    @GetMapping("/api/reviews/reporting/daily")
    List<ReviewAgg> dailyAgg();
}
