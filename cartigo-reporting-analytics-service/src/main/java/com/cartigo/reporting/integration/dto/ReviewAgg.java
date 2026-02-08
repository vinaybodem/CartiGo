package com.cartigo.reporting.integration.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReviewAgg {
    private LocalDate date;
    private Long reviewsCount;
    private BigDecimal avgRating;

    public ReviewAgg() {}

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Long getReviewsCount() { return reviewsCount; }
    public void setReviewsCount(Long reviewsCount) { this.reviewsCount = reviewsCount; }

    public BigDecimal getAvgRating() { return avgRating; }
    public void setAvgRating(BigDecimal avgRating) { this.avgRating = avgRating; }
}
