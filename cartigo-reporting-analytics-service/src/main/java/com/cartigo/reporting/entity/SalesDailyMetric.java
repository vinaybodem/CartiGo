package com.cartigo.reporting.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "sales_daily_metrics",
        uniqueConstraints = @UniqueConstraint(name="uk_sales_day", columnNames = {"metric_date"})
)
public class SalesDailyMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="metric_date", nullable = false)
    private LocalDate metricDate;

    @Column(name="orders_count", nullable = false)
    private Long ordersCount = 0L;

    @Column(name="gross_sales", nullable = false, precision = 14, scale = 2)
    private BigDecimal grossSales = BigDecimal.ZERO;

    @Column(name="delivered_orders", nullable = false)
    private Long deliveredOrders = 0L;

    @Column(name="cancelled_orders", nullable = false)
    private Long cancelledOrders = 0L;

    public SalesDailyMetric() {}

    public Long getId() { return id; }

    public LocalDate getMetricDate() { return metricDate; }
    public void setMetricDate(LocalDate metricDate) { this.metricDate = metricDate; }

    public Long getOrdersCount() { return ordersCount; }
    public void setOrdersCount(Long ordersCount) { this.ordersCount = ordersCount; }

    public BigDecimal getGrossSales() { return grossSales; }
    public void setGrossSales(BigDecimal grossSales) { this.grossSales = grossSales; }

    public Long getDeliveredOrders() { return deliveredOrders; }
    public void setDeliveredOrders(Long deliveredOrders) { this.deliveredOrders = deliveredOrders; }

    public Long getCancelledOrders() { return cancelledOrders; }
    public void setCancelledOrders(Long cancelledOrders) { this.cancelledOrders = cancelledOrders; }
}
