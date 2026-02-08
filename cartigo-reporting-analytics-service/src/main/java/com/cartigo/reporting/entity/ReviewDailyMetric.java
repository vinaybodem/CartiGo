package com.cartigo.reporting.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "review_daily_metrics",
        uniqueConstraints = @UniqueConstraint(name="uk_review_day", columnNames = {"metric_date"})
)
public class ReviewDailyMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="metric_date", nullable = false)
    private LocalDate metricDate;

    @Column(name="reviews_count", nullable = false)
    private Long reviewsCount = 0L;

    @Column(name="avg_rating", nullable = false, precision = 4, scale = 2)
    private BigDecimal avgRating = BigDecimal.ZERO;

    public ReviewDailyMetric() {}

    public Long getId() { return id; }

    public LocalDate getMetricDate() { return metricDate; }
    public void setMetricDate(LocalDate metricDate) { this.metricDate = metricDate; }

    public Long getReviewsCount() { return reviewsCount; }
    public void setReviewsCount(Long reviewsCount) { this.reviewsCount = reviewsCount; }

    public BigDecimal getAvgRating() { return avgRating; }
    public void setAvgRating(BigDecimal avgRating) { this.avgRating = avgRating; }
}
