package com.cartigo.reporting.repository;

import com.cartigo.reporting.entity.ReturnDailyMetric;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReturnDailyMetricRepository extends JpaRepository<ReturnDailyMetric, Long> {
    Optional<ReturnDailyMetric> findByMetricDate(LocalDate metricDate);
    List<ReturnDailyMetric> findTop60ByOrderByMetricDateDesc();
}
