package com.cartigo.reporting.service;

import com.cartigo.reporting.entity.ReturnDailyMetric;
import com.cartigo.reporting.entity.ReviewDailyMetric;
import com.cartigo.reporting.entity.SalesDailyMetric;
import com.cartigo.reporting.integration.OrderReportingClient;
import com.cartigo.reporting.integration.ReturnReportingClient;
import com.cartigo.reporting.integration.ReviewReportingClient;
import com.cartigo.reporting.integration.dto.OrderAgg;
import com.cartigo.reporting.integration.dto.ReturnAgg;
import com.cartigo.reporting.integration.dto.ReviewAgg;
import com.cartigo.reporting.repository.ReturnDailyMetricRepository;
import com.cartigo.reporting.repository.ReviewDailyMetricRepository;
import com.cartigo.reporting.repository.SalesDailyMetricRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Pull "daily aggregates" from other services and store into reporting DB.
 * If you don't have reporting endpoints in other services yet, disable in application.yml.
 */
@Component
public class AggregationJob {

    private static final Logger log = LoggerFactory.getLogger(AggregationJob.class);

    private final SalesDailyMetricRepository salesRepo;
    private final ReturnDailyMetricRepository returnRepo;
    private final ReviewDailyMetricRepository reviewRepo;

    private final OrderReportingClient orderClient;
    private final ReturnReportingClient returnClient;
    private final ReviewReportingClient reviewClient;

    @Value("${reporting.aggregation.enabled:true}")
    private boolean enabled;

    @Value("${reporting.sources.orders.enabled:true}")
    private boolean ordersEnabled;

    @Value("${reporting.sources.returns.enabled:true}")
    private boolean returnsEnabled;

    @Value("${reporting.sources.reviews.enabled:true}")
    private boolean reviewsEnabled;

    public AggregationJob(SalesDailyMetricRepository salesRepo,
                          ReturnDailyMetricRepository returnRepo,
                          ReviewDailyMetricRepository reviewRepo,
                          OrderReportingClient orderClient,
                          ReturnReportingClient returnClient,
                          ReviewReportingClient reviewClient) {
        this.salesRepo = salesRepo;
        this.returnRepo = returnRepo;
        this.reviewRepo = reviewRepo;
        this.orderClient = orderClient;
        this.returnClient = returnClient;
        this.reviewClient = reviewClient;
    }

    @Scheduled(cron = "${reporting.aggregation.cron:0 */15 * * * *}")
    public void run() {
        if (!enabled) return;

        if (ordersEnabled) {
            try {
                List<OrderAgg> daily = orderClient.dailyAgg();
                for (OrderAgg a : daily) {
                    SalesDailyMetric m = salesRepo.findByMetricDate(a.getDate()).orElseGet(SalesDailyMetric::new);
                    m.setMetricDate(a.getDate());
                    m.setOrdersCount(nvl(a.getOrdersCount()));
                    m.setGrossSales(a.getGrossSales() != null ? a.getGrossSales() : BigDecimal.ZERO);
                    m.setDeliveredOrders(nvl(a.getDeliveredOrders()));
                    m.setCancelledOrders(nvl(a.getCancelledOrders()));
                    salesRepo.save(m);
                }
            } catch (Exception e) {
                log.warn("Order aggregation failed (order-service reporting endpoint missing or down): {}", e.getMessage());
            }
        }

        if (returnsEnabled) {
            try {
                List<ReturnAgg> daily = returnClient.dailyAgg();
                for (ReturnAgg a : daily) {
                    ReturnDailyMetric m = returnRepo.findByMetricDate(a.getDate()).orElseGet(ReturnDailyMetric::new);
                    m.setMetricDate(a.getDate());
                    m.setReturnRequests(nvl(a.getReturnRequests()));
                    m.setApprovedReturns(nvl(a.getApprovedReturns()));
                    m.setRejectedReturns(nvl(a.getRejectedReturns()));
                    m.setRefundAmountTotal(a.getRefundAmountTotal() != null ? a.getRefundAmountTotal() : BigDecimal.ZERO);
                    returnRepo.save(m);
                }
            } catch (Exception e) {
                log.warn("Return aggregation failed (return-refund reporting endpoint missing or down): {}", e.getMessage());
            }
        }

        if (reviewsEnabled) {
            try {
                List<ReviewAgg> daily = reviewClient.dailyAgg();
                for (ReviewAgg a : daily) {
                    ReviewDailyMetric m = reviewRepo.findByMetricDate(a.getDate()).orElseGet(ReviewDailyMetric::new);
                    m.setMetricDate(a.getDate());
                    m.setReviewsCount(nvl(a.getReviewsCount()));
                    m.setAvgRating(a.getAvgRating() != null ? a.getAvgRating() : BigDecimal.ZERO);
                    reviewRepo.save(m);
                }
            } catch (Exception e) {
                log.warn("Review aggregation failed (review-service reporting endpoint missing or down): {}", e.getMessage());
            }
        }
    }

    private long nvl(Long v) {
        return v == null ? 0L : v;
    }
}
