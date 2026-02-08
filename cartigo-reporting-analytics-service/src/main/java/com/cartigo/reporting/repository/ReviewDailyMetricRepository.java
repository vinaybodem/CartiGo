package com.cartigo.reporting.repository;

import com.cartigo.reporting.entity.ReviewDailyMetric;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReviewDailyMetricRepository extends JpaRepository<ReviewDailyMetric, Long> {
    Optional<ReviewDailyMetric> findByMetricDate(LocalDate metricDate);
    List<ReviewDailyMetric> findTop60ByOrderByMetricDateDesc();
}
