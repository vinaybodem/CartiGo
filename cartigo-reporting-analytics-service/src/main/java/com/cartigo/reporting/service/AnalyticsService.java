package com.cartigo.reporting.service;

import com.cartigo.reporting.dto.DashboardSummaryResponse;
import com.cartigo.reporting.entity.ReturnDailyMetric;
import com.cartigo.reporting.entity.ReviewDailyMetric;
import com.cartigo.reporting.entity.SalesDailyMetric;
import com.cartigo.reporting.repository.ReturnDailyMetricRepository;
import com.cartigo.reporting.repository.ReviewDailyMetricRepository;
import com.cartigo.reporting.repository.SalesDailyMetricRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AnalyticsService {

    private final SalesDailyMetricRepository salesRepo;
    private final ReturnDailyMetricRepository returnRepo;
    private final ReviewDailyMetricRepository reviewRepo;

    public AnalyticsService(SalesDailyMetricRepository salesRepo,
                            ReturnDailyMetricRepository returnRepo,
                            ReviewDailyMetricRepository reviewRepo) {
        this.salesRepo = salesRepo;
        this.returnRepo = returnRepo;
        this.reviewRepo = reviewRepo;
    }

    public DashboardSummaryResponse summaryToday() {
        LocalDate today = LocalDate.now();

        SalesDailyMetric s = salesRepo.findByMetricDate(today).orElse(null);
        ReturnDailyMetric r = returnRepo.findByMetricDate(today).orElse(null);
        ReviewDailyMetric rv = reviewRepo.findByMetricDate(today).orElse(null);

        DashboardSummaryResponse out = new DashboardSummaryResponse();
        out.setOrdersToday(s != null ? s.getOrdersCount() : 0L);
        out.setGrossSalesToday(s != null ? s.getGrossSales() : java.math.BigDecimal.ZERO);
        out.setReturnsToday(r != null ? r.getReturnRequests() : 0L);
        out.setReviewsToday(rv != null ? rv.getReviewsCount() : 0L);
        out.setAvgRatingToday(rv != null ? rv.getAvgRating() : java.math.BigDecimal.ZERO);
        return out;
    }

    public List<SalesDailyMetric> salesLast60() {
        return salesRepo.findTop60ByOrderByMetricDateDesc();
    }

    public List<ReturnDailyMetric> returnsLast60() {
        return returnRepo.findTop60ByOrderByMetricDateDesc();
    }

    public List<ReviewDailyMetric> reviewsLast60() {
        return reviewRepo.findTop60ByOrderByMetricDateDesc();
    }
}
