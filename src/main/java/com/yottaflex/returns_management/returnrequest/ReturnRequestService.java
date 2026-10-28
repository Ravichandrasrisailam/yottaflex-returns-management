
package com.yottaflex.returns_management.returnrequest;

import com.yottaflex.returns_management.order.Order;
import com.yottaflex.returns_management.order.OrderRepository;
import com.yottaflex.returns_management.returnrequest.dto.CreateReturnRequest;
import com.yottaflex.returns_management.returnrequest.dto.ReturnRequestResponse;
import com.yottaflex.returns_management.inventory.InventoryDispositionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReturnRequestService {

    private final ReturnRequestRepository returnRequestRepository;
    private final OrderRepository orderRepository;
    private final ReturnLineRepository returnLineRepository;
    private final InspectionRepository inspectionRepository;
    private final InventoryDispositionRepository dispositionRepository;

    public ReturnRequestService(
            ReturnRequestRepository returnRequestRepository,
            OrderRepository orderRepository,
            ReturnLineRepository returnLineRepository,
            InspectionRepository inspectionRepository,
            InventoryDispositionRepository dispositionRepository) {
        this.returnRequestRepository = returnRequestRepository;
        this.orderRepository = orderRepository;
        this.returnLineRepository = returnLineRepository;
        this.inspectionRepository = inspectionRepository;
        this.dispositionRepository = dispositionRepository;
    }

    @Transactional
    public ReturnRequestResponse create(CreateReturnRequest request) {

        if (returnRequestRepository.existsByReturnCode(request.returnCode())) {
            throw new IllegalArgumentException(
                    "Return code already exists: " + request.returnCode());
        }

        Order order = orderRepository.findByOrderCode(request.orderCode())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Order not found: " + request.orderCode()));

        ReturnRequest returnRequest = new ReturnRequest(
                request.returnCode(),
                order,
                request.reason()
        );

        ReturnRequest saved = returnRequestRepository.save(returnRequest);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public ReturnRequestResponse getByCode(String returnCode) {

        ReturnRequest returnRequest =
                returnRequestRepository.findByReturnCode(returnCode)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Return request not found: " + returnCode));

        return toResponse(returnRequest);
    }

    @Transactional
    public ReturnRequestResponse markInspected(String returnCode) {

        ReturnRequest request = returnRequestRepository
                .findByReturnCode(returnCode)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Return request not found: " + returnCode));

        if (request.getStatus() != ReturnRequestStatus.APPROVED) {
            throw new IllegalArgumentException(
                    "Only approved return requests can be marked inspected");
        }

        List<ReturnLine> lines =
                returnLineRepository.findByReturnRequest_Id(request.getId());

        if (lines.isEmpty()) {
            throw new IllegalArgumentException(
                    "The return request must contain at least one return line");
        }

        for (ReturnLine line : lines) {
            Inspection inspection = inspectionRepository
                    .findByReturnLine_Id(line.getId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Every return line must be inspected first"));

            dispositionRepository.findByInspection_Id(inspection.getId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Every inspection must have an inventory disposition"));
        }

        request.setStatus(ReturnRequestStatus.INSPECTED);

        return toResponse(returnRequestRepository.save(request));
    }

    private ReturnRequestResponse toResponse(ReturnRequest request) {
        return new ReturnRequestResponse(
                request.getId(),
                request.getReturnCode(),
                request.getOrder().getOrderCode(),
                request.getStatus(),
                request.getReason(),
                request.getRequestedAt()
        );
    }
}
