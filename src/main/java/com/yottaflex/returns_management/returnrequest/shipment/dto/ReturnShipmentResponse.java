
package com.yottaflex.returns_management.returnrequest.shipment.dto;

import com.yottaflex.returns_management.returnrequest.shipment.ReturnShipmentStatus;
import java.time.LocalDateTime;

public record ReturnShipmentResponse(
        Long id,
        String shipmentCode,
        String returnCode,
        ReturnShipmentStatus status,
        String carrier,
        String trackingNumber,
        LocalDateTime shippedAt,
        LocalDateTime deliveredAt
) {
}
