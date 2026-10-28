
package com.yottaflex.returns_management.returnrequest.shipment;

import com.yottaflex.returns_management.returnrequest.shipment.dto.CreateReturnShipmentRequest;
import com.yottaflex.returns_management.returnrequest.shipment.dto.ReturnShipmentResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/return-shipments")
public class ReturnShipmentController {

    private final ReturnShipmentService shipmentService;

    public ReturnShipmentController(ReturnShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReturnShipmentResponse create(
            @Valid @RequestBody CreateReturnShipmentRequest request) {
        return shipmentService.create(request);
    }

    @PostMapping("/{shipmentCode}/in-transit")
    public ReturnShipmentResponse markInTransit(
            @PathVariable String shipmentCode) {
        return shipmentService.markInTransit(shipmentCode);
    }

    @PostMapping("/{shipmentCode}/delivered")
    public ReturnShipmentResponse markDelivered(
            @PathVariable String shipmentCode) {
        return shipmentService.markDelivered(shipmentCode);
    }

    @GetMapping("/return/{returnCode}")
    public List<ReturnShipmentResponse> getByReturnCode(
            @PathVariable String returnCode) {
        return shipmentService.getByReturnCode(returnCode);
    }
}
