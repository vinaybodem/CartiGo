package com.cartigo.reporting.controller;

import com.cartigo.reporting.dto.DashboardSummaryResponse;
import com.cartigo.reporting.entity.ReturnDailyMetric;
import com.cartigo.reporting.entity.ReviewDailyMetric;
import com.cartigo.reporting.entity.SalesDailyMetric;
import com.cartigo.reporting.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Power BI can connect using "Web" connector to these JSON endpoints.
 */
@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService service;

    public AnalyticsController(AnalyticsService service) {
        this.service = service;
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> summary() {
        return ResponseEntity.ok(service.summaryToday());
    }

    @GetMapping("/sales/daily")
    public ResponseEntity<List<SalesDailyMetric>> salesDaily() {
        return ResponseEntity.ok(service.salesLast60());
    }

    @GetMapping("/returns/daily")
    public ResponseEntity<List<ReturnDailyMetric>> returnsDaily() {
        return ResponseEntity.ok(service.returnsLast60());
    }

    @GetMapping("/reviews/daily")
    public ResponseEntity<List<ReviewDailyMetric>> reviewsDaily() {
        return ResponseEntity.ok(service.reviewsLast60());
    }
}
