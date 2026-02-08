package com.cartigo.reporting.integration.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class OrderAgg {
    private LocalDate date;
    private Long ordersCount;
    private BigDecimal grossSales;
    private Long deliveredOrders;
    private Long cancelledOrders;

    public OrderAgg() {}

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Long getOrdersCount() { return ordersCount; }
    public void setOrdersCount(Long ordersCount) { this.ordersCount = ordersCount; }

    public BigDecimal getGrossSales() { return grossSales; }
    public void setGrossSales(BigDecimal grossSales) { this.grossSales = grossSales; }

    public Long getDeliveredOrders() { return deliveredOrders; }
    public void setDeliveredOrders(Long deliveredOrders) { this.deliveredOrders = deliveredOrders; }

    public Long getCancelledOrders() { return cancelledOrders; }
    public void setCancelledOrders(Long cancelledOrders) { this.cancelledOrders = cancelledOrders; }
}
