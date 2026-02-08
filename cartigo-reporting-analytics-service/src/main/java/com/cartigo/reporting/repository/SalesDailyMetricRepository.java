package com.cartigo.reporting.repository;

import com.cartigo.reporting.entity.SalesDailyMetric;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SalesDailyMetricRepository extends JpaRepository<SalesDailyMetric, Long> {
    Optional<SalesDailyMetric> findByMetricDate(LocalDate metricDate);
    List<SalesDailyMetric> findTop60ByOrderByMetricDateDesc();
}
