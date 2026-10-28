
package com.yottaflex.returns_management.returnrequest.shipment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateReturnShipmentRequest(
        @NotBlank
        @Size(max = 30)
        String shipmentCode,

        @NotBlank
        @Size(max = 30)
        String returnCode,

        @Size(max = 100)
        String carrier,

        @Size(max = 100)
        String trackingNumber
) {
}
