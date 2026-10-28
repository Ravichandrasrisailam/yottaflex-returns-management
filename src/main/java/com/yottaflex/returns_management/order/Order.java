
package com.yottaflex.returns_management.order;

import com.yottaflex.returns_management.customer.Customer;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "customer_orders",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_customer_orders_order_code",
        columnNames = "order_code"
    )
)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 30)
    @Column(name = "order_code", nullable = false, unique = true, length = 30)
    private String orderCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @NotNull
    @Column(name = "ordered_at", nullable = false)
    private LocalDateTime orderedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @NotNull
    @Column(name = "total_paid", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPaid;

    protected Order() {
        // Required by JPA.
    }

    // Existing constructor retained for compatibility.
    public Order(
            String orderCode,
            Customer customer,
            OrderStatus status,
            LocalDateTime orderedAt,
            LocalDateTime deliveredAt) {
        this(
            orderCode,
            customer,
            status,
            orderedAt,
            deliveredAt,
            BigDecimal.ZERO
        );
    }

    public Order(
            String orderCode,
            Customer customer,
            OrderStatus status,
            LocalDateTime orderedAt,
            LocalDateTime deliveredAt,
            BigDecimal totalPaid) {
        this.orderCode = orderCode;
        this.customer = customer;
        this.status = status;
        this.orderedAt = orderedAt;
        this.deliveredAt = deliveredAt;
        this.totalPaid = totalPaid;
    }

    public Long getId() {
        return id;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public Customer getCustomer() {
        return customer;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getOrderedAt() {
        return orderedAt;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public BigDecimal getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(BigDecimal totalPaid) {
        this.totalPaid = totalPaid;
    }
}
