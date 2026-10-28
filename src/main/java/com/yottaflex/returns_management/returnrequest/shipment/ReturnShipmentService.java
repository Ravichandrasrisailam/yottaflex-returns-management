
package com.yottaflex.returns_management.returnrequest.shipment;

import com.yottaflex.returns_management.returnrequest.ReturnRequest;
import com.yottaflex.returns_management.returnrequest.ReturnRequestRepository;
import com.yottaflex.returns_management.returnrequest.ReturnRequestStatus;
import com.yottaflex.returns_management.returnrequest.shipment.dto.CreateReturnShipmentRequest;
import com.yottaflex.returns_management.returnrequest.shipment.dto.ReturnShipmentResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReturnShipmentService {

    private final ReturnShipmentRepository shipmentRepository;
    private final ReturnRequestRepository returnRequestRepository;

    public ReturnShipmentService(
            ReturnShipmentRepository shipmentRepository,
            ReturnRequestRepository returnRequestRepository) {
        this.shipmentRepository = shipmentRepository;
        this.returnRequestRepository = returnRequestRepository;
    }

    @Transactional
    public ReturnShipmentResponse create(CreateReturnShipmentRequest request) {

        if (shipmentRepository.existsByShipmentCode(request.shipmentCode())) {
            throw new IllegalArgumentException(
                    "Shipment code already exists: " + request.shipmentCode());
        }

        ReturnRequest returnRequest = returnRequestRepository
                .findByReturnCode(request.returnCode())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Return request not found: " + request.returnCode()));

        if (returnRequest.getStatus() != ReturnRequestStatus.APPROVED) {
            throw new IllegalArgumentException(
                    "A shipment can only be created for an approved return");
        }

        ReturnShipment shipment = new ReturnShipment(
                request.shipmentCode(),
                returnRequest,
                request.carrier(),
                request.trackingNumber()
        );

        return toResponse(shipmentRepository.save(shipment));
    }

    @Transactional
    public ReturnShipmentResponse markInTransit(String shipmentCode) {
        ReturnShipment shipment = findShipment(shipmentCode);
        shipment.markInTransit();
        return toResponse(shipmentRepository.save(shipment));
    }

    @Transactional
    public ReturnShipmentResponse markDelivered(String shipmentCode) {
        ReturnShipment shipment = findShipment(shipmentCode);
        shipment.markDelivered();
        return toResponse(shipmentRepository.save(shipment));
    }

    @Transactional(readOnly = true)
    public List<ReturnShipmentResponse> getByReturnCode(String returnCode) {

        ReturnRequest returnRequest = returnRequestRepository
                .findByReturnCode(returnCode)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Return request not found: " + returnCode));

        return shipmentRepository.findByReturnRequestId(returnRequest.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ReturnShipment findShipment(String shipmentCode) {
        return shipmentRepository.findByShipmentCode(shipmentCode)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Shipment not found: " + shipmentCode));
    }

    private ReturnShipmentResponse toResponse(ReturnShipment shipment) {
        return new ReturnShipmentResponse(
                shipment.getId(),
                shipment.getShipmentCode(),
                shipment.getReturnRequest().getReturnCode(),
                shipment.getStatus(),
                shipment.getCarrier(),
                shipment.getTrackingNumber(),
                shipment.getShippedAt(),
                shipment.getDeliveredAt()
        );
    }
}
