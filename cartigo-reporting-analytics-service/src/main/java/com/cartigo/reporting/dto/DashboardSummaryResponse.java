package com.cartigo.reporting.dto;

import java.math.BigDecimal;

public class DashboardSummaryResponse {
    private Long ordersToday;
    private BigDecimal grossSalesToday;
    private Long returnsToday;
    private Long reviewsToday;
    private BigDecimal avgRatingToday;

    public DashboardSummaryResponse() {}

    public Long getOrdersToday() { return ordersToday; }
    public void setOrdersToday(Long ordersToday) { this.ordersToday = ordersToday; }

    public BigDecimal getGrossSalesToday() { return grossSalesToday; }
    public void setGrossSalesToday(BigDecimal grossSalesToday) { this.grossSalesToday = grossSalesToday; }

    public Long getReturnsToday() { return returnsToday; }
    public void setReturnsToday(Long returnsToday) { this.returnsToday = returnsToday; }

    public Long getReviewsToday() { return reviewsToday; }
    public void setReviewsToday(Long reviewsToday) { this.reviewsToday = reviewsToday; }

    public BigDecimal getAvgRatingToday() { return avgRatingToday; }
    public void setAvgRatingToday(BigDecimal avgRatingToday) { this.avgRatingToday = avgRatingToday; }
}
