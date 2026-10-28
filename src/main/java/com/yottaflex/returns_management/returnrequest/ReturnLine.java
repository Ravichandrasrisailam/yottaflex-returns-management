
package com.yottaflex.returns_management.returnrequest;

import com.yottaflex.returns_management.order.OrderLine;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(
    name = "return_lines",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_return_request_order_line",
        columnNames = {"return_request_id", "order_line_id"}
    )
)
public class ReturnLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "return_request_id", nullable = false)
    private ReturnRequest returnRequest;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_line_id", nullable = false)
    private OrderLine orderLine;

    @NotNull
    @Min(1)
    @Column(name = "quantity_requested", nullable = false)
    private Integer quantityRequested;

    protected ReturnLine() {
    }

    public ReturnLine(
            ReturnRequest returnRequest,
            OrderLine orderLine,
            Integer quantityRequested) {
        this.returnRequest = returnRequest;
        this.orderLine = orderLine;
        this.quantityRequested = quantityRequested;
    }

    public Long getId() {
        return id;
    }

    public ReturnRequest getReturnRequest() {
        return returnRequest;
    }

    public OrderLine getOrderLine() {
        return orderLine;
    }

    public Integer getQuantityRequested() {
        return quantityRequested;
    }
}
