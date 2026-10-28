
package com.yottaflex.returns_management.returnrequest.shipment;

import com.yottaflex.returns_management.returnrequest.ReturnRequest;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "return_shipments",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_return_shipment_code",
        columnNames = "shipment_code"
    )
)
public class ReturnShipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 30)
    @Column(name = "shipment_code", nullable = false, unique = true, length = 30)
    private String shipmentCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "return_request_id", nullable = false)
    private ReturnRequest returnRequest;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ReturnShipmentStatus status;

    @Size(max = 100)
    @Column(name = "carrier", length = 100)
    private String carrier;

    @Size(max = 100)
    @Column(name = "tracking_number", length = 100)
    private String trackingNumber;

    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    protected ReturnShipment() {
    }

    public ReturnShipment(
            String shipmentCode,
            ReturnRequest returnRequest,
            String carrier,
            String trackingNumber) {
        this.shipmentCode = shipmentCode;
        this.returnRequest = returnRequest;
        this.carrier = carrier;
        this.trackingNumber = trackingNumber;
        this.status = ReturnShipmentStatus.LABEL_CREATED;
    }

    public Long getId() {
        return id;
    }

    public String getShipmentCode() {
        return shipmentCode;
    }

    public ReturnRequest getReturnRequest() {
        return returnRequest;
    }

    public ReturnShipmentStatus getStatus() {
        return status;
    }

    public String getCarrier() {
        return carrier;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public LocalDateTime getShippedAt() {
        return shippedAt;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public void markInTransit() {
        if (status != ReturnShipmentStatus.LABEL_CREATED) {
            throw new IllegalStateException(
                    "Only shipments with a created label can enter transit");
        }
        status = ReturnShipmentStatus.IN_TRANSIT;
        shippedAt = LocalDateTime.now();
    }

    public void markDelivered() {
        if (status != ReturnShipmentStatus.IN_TRANSIT) {
            throw new IllegalStateException(
                    "Only in-transit shipments can be delivered");
        }
        status = ReturnShipmentStatus.DELIVERED;
        deliveredAt = LocalDateTime.now();
    }
}
