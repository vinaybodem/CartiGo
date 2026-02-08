    package com.cartigo.reporting.entity;

    import jakarta.persistence.*;

    import java.math.BigDecimal;
    import java.time.LocalDate;

    @Entity
    @Table(
            name = "return_daily_metrics",
            uniqueConstraints = @UniqueConstraint(name="uk_return_day", columnNames = {"metric_date"})
    )
    public class ReturnDailyMetric {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name="metric_date", nullable = false)
        private LocalDate metricDate;

        @Column(name="return_requests", nullable = false)
        private Long returnRequests = 0L;

        @Column(name="approved_returns", nullable = false)
        private Long approvedReturns = 0L;

        @Column(name="rejected_returns", nullable = false)
        private Long rejectedReturns = 0L;

        @Column(name="refund_amount_total", nullable = false, precision = 14, scale = 2)
        private BigDecimal refundAmountTotal = BigDecimal.ZERO;

        public ReturnDailyMetric() {}

        public Long getId() { return id; }

        public LocalDate getMetricDate() { return metricDate; }
        public void setMetricDate(LocalDate metricDate) { this.metricDate = metricDate; }

        public Long getReturnRequests() { return returnRequests; }
        public void setReturnRequests(Long returnRequests) { this.returnRequests = returnRequests; }

        public Long getApprovedReturns() { return approvedReturns; }
        public void setApprovedReturns(Long approvedReturns) { this.approvedReturns = approvedReturns; }

        public Long getRejectedReturns() { return rejectedReturns; }
        public void setRejectedReturns(Long rejectedReturns) { this.rejectedReturns = rejectedReturns; }

        public BigDecimal getRefundAmountTotal() { return refundAmountTotal; }
        public void setRefundAmountTotal(BigDecimal refundAmountTotal) { this.refundAmountTotal = refundAmountTotal; }
    }
