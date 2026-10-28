
package com.yottaflex.returns_management.refund;

import com.yottaflex.returns_management.returnrequest.ReturnRequest;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "refunds",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_refund_return_request",
            columnNames = "return_request_id"
        )
    }
)
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "return_request_id",
        nullable = false,
        unique = true
    )
    private ReturnRequest returnRequest;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected Refund() {
    }

    public Refund(
            ReturnRequest returnRequest,
            BigDecimal amount,
            String status) {
        this.returnRequest = returnRequest;
        this.amount = amount;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public ReturnRequest getReturnRequest() {
        return returnRequest;
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

    public void setStatus(String status) {
        this.status = status;
    }
}
