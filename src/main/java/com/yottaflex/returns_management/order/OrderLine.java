
package com.yottaflex.returns_management.order;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Entity
@Table(
    name = "order_lines",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_order_line_number",
        columnNames = {"order_id", "line_number"}
    )
)
public class OrderLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @NotNull
    @Min(1)
    @Column(name = "line_number", nullable = false)
    private Integer lineNumber;

    @NotBlank
    @Size(max = 60)
    @Column(name = "sku", nullable = false, length = 60)
    private String sku;

    @NotNull
    @Min(1)
    @Column(nullable = false)
    private Integer quantity;

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    protected OrderLine() {
        // Required by JPA.
    }

    public OrderLine(
            Order order,
            Integer lineNumber,
            String sku,
            Integer quantity,
            BigDecimal unitPrice) {
        this.order = order;
        this.lineNumber = lineNumber;
        this.sku = sku;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public Long getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public String getSku() {
        return sku;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
}
