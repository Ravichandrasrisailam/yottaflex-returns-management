
package com.yottaflex.returns_management.refund;

import com.yottaflex.returns_management.inventory.InventoryDisposition;
import com.yottaflex.returns_management.inventory.InventoryDispositionRepository;
import com.yottaflex.returns_management.order.Order;
import com.yottaflex.returns_management.returnrequest.ReturnLine;
import com.yottaflex.returns_management.returnrequest.ReturnLineRepository;
import com.yottaflex.returns_management.returnrequest.ReturnRequest;
import com.yottaflex.returns_management.returnrequest.ReturnRequestRepository;
import com.yottaflex.returns_management.returnrequest.ReturnRequestStatus;
import com.yottaflex.returns_management.returnrequest.Inspection;
import com.yottaflex.returns_management.returnrequest.InspectionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class RefundService {

    private final RefundRepository refundRepository;
    private final ReturnRequestRepository returnRequestRepository;
    private final ReturnLineRepository returnLineRepository;
    private final InspectionRepository inspectionRepository;
    private final InventoryDispositionRepository dispositionRepository;

    public RefundService(
            RefundRepository refundRepository,
            ReturnRequestRepository returnRequestRepository,
            ReturnLineRepository returnLineRepository,
            InspectionRepository inspectionRepository,
            InventoryDispositionRepository dispositionRepository) {

        this.refundRepository = refundRepository;
        this.returnRequestRepository = returnRequestRepository;
        this.returnLineRepository = returnLineRepository;
        this.inspectionRepository = inspectionRepository;
        this.dispositionRepository = dispositionRepository;
    }

    @Transactional
    public RefundResponse createRefund(RefundRequest request) {

        ReturnRequest returnRequest = returnRequestRepository
                .findById(request.getReturnRequestId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Return request not found"));

        if (refundRepository.existsByReturnRequest_Id(returnRequest.getId())) {
            throw new IllegalArgumentException(
                    "A refund already exists for this return request");
        }

        if (returnRequest.getStatus() != ReturnRequestStatus.INSPECTED
                && returnRequest.getStatus() != ReturnRequestStatus.REFUND_PENDING) {
            throw new IllegalArgumentException(
                    "Return request must be inspected or refund pending");
        }

        List<ReturnLine> returnLines =
                returnLineRepository.findByReturnRequest_Id(returnRequest.getId());

        if (returnLines.isEmpty()) {
            throw new IllegalArgumentException(
                    "Cannot refund a return request without return lines");
        }

        BigDecimal totalRefund = BigDecimal.ZERO;

        for (ReturnLine line : returnLines) {

            Inspection inspection = inspectionRepository
                    .findByReturnLine_Id(line.getId())
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Every return line must be inspected before refund"));

            InventoryDisposition disposition = dispositionRepository
                    .findByInspection_Id(inspection.getId())
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Every inspected line must have an inventory disposition"));

            // Accessing the disposition also confirms that the inspection
            // has an inventory decision recorded.
            if (disposition.getId() == null) {
                throw new IllegalArgumentException(
                        "Inventory disposition must be saved before refund");
            }

            BigDecimal lineRefund = line.getOrderLine()
                    .getUnitPrice()
                    .multiply(BigDecimal.valueOf(line.getQuantityRequested()));

            totalRefund = totalRefund.add(lineRefund);
        }

        if (totalRefund.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Refund amount must be greater than zero");
        }

        Order order = returnLines.get(0).getOrderLine().getOrder();
        BigDecimal totalPaid = order.getTotalPaid();

        if (totalPaid == null || totalPaid.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "A valid payment amount must be recorded for the order");
        }

        // Include existing refunds, including those still PENDING.
        BigDecimal previousRefunds =
                refundRepository.sumRefundsForOrder(order.getId());

        if (previousRefunds == null) {
            previousRefunds = BigDecimal.ZERO;
        }

        BigDecimal cumulativeRefund = previousRefunds.add(totalRefund);

        if (cumulativeRefund.compareTo(totalPaid) > 0) {
            throw new IllegalArgumentException(
                    "Refund exceeds the remaining refundable amount. "
                    + "Total paid: " + totalPaid
                    + ", previous refunds: " + previousRefunds
                    + ", requested refund: " + totalRefund);
        }

        Refund refund = new Refund(returnRequest, totalRefund, "PENDING");
        Refund savedRefund = refundRepository.save(refund);

        return new RefundResponse(
                savedRefund.getId(),
                returnRequest.getId(),
                returnRequest.getReturnCode(),
                savedRefund.getAmount(),
                savedRefund.getStatus(),
                savedRefund.getCreatedAt());
    }
}
