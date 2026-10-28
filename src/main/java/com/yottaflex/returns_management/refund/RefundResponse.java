
package com.yottaflex.returns_management.refund;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RefundResponse {

    private Long refundId;
    private Long returnRequestId;
    private String returnCode;
    private BigDecimal amount;
    private String status;
    private LocalDateTime createdAt;

    public RefundResponse(
            Long refundId,
            Long returnRequestId,
            String returnCode,
            BigDecimal amount,
            String status,
            LocalDateTime createdAt) {
        this.refundId = refundId;
        this.returnRequestId = returnRequestId;
        this.returnCode = returnCode;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getRefundId() {
        return refundId;
    }

    public Long getReturnRequestId() {
        return returnRequestId;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
