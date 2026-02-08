package com.cartigo.reporting.integration.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReturnAgg {
    private LocalDate date;
    private Long returnRequests;
    private Long approvedReturns;
    private Long rejectedReturns;
    private BigDecimal refundAmountTotal;

    public ReturnAgg() {}

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Long getReturnRequests() { return returnRequests; }
    public void setReturnRequests(Long returnRequests) { this.returnRequests = returnRequests; }

    public Long getApprovedReturns() { return approvedReturns; }
    public void setApprovedReturns(Long approvedReturns) { this.approvedReturns = approvedReturns; }

    public Long getRejectedReturns() { return rejectedReturns; }
    public void setRejectedReturns(Long rejectedReturns) { this.rejectedReturns = rejectedReturns; }

    public BigDecimal getRefundAmountTotal() { return refundAmountTotal; }
    public void setRefundAmountTotal(BigDecimal refundAmountTotal) { this.refundAmountTotal = refundAmountTotal; }
}
