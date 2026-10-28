
package com.yottaflex.returns_management.returnrequest;

import com.yottaflex.returns_management.order.Order;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "return_requests",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_return_request_code",
            columnNames = "return_code"
        )
    }
)
public class ReturnRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 30)
    @Column(name = "return_code", nullable = false, unique = true, length = 30)
    private String returnCode;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ReturnRequestStatus status;

    @NotBlank
    @Size(max = 500)
    @Column(name = "reason", nullable = false, length = 500)
    private String reason;

    @Column(name = "requested_at", nullable = false, updatable = false)
    private LocalDateTime requestedAt;

    protected ReturnRequest() {
    }

    public ReturnRequest(
            String returnCode,
            Order order,
            String reason) {
        this.returnCode = returnCode;
        this.order = order;
        this.reason = reason;
        this.status = ReturnRequestStatus.SUBMITTED;
    }

    @PrePersist
    protected void onCreate() {
        if (requestedAt == null) {
            requestedAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public Order getOrder() {
        return order;
    }

    public ReturnRequestStatus getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setStatus(ReturnRequestStatus status) {
        this.status = status;
    }
}
