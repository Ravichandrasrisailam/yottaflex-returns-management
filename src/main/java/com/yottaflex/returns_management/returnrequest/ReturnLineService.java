
package com.yottaflex.returns_management.returnrequest;

import com.yottaflex.returns_management.order.OrderLine;
import com.yottaflex.returns_management.order.OrderLineRepository;
import com.yottaflex.returns_management.order.Order;
import com.yottaflex.returns_management.order.OrderRepository;
import com.yottaflex.returns_management.returnrequest.dto.CreateReturnLineRequest;
import com.yottaflex.returns_management.returnrequest.dto.ReturnLineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReturnLineService {

    private final ReturnRequestRepository returnRequestRepository;
    private final ReturnLineRepository returnLineRepository;
    private final OrderLineRepository orderLineRepository;
    private final OrderRepository orderRepository;

    public ReturnLineService(
            ReturnRequestRepository returnRequestRepository,
            ReturnLineRepository returnLineRepository,
            OrderLineRepository orderLineRepository,
            OrderRepository orderRepository) {
        this.returnRequestRepository = returnRequestRepository;
        this.returnLineRepository = returnLineRepository;
        this.orderLineRepository = orderLineRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public ReturnLineResponse addLine(
            String returnCode,
            CreateReturnLineRequest request) {

        ReturnRequest returnRequest =
                returnRequestRepository.findByReturnCode(returnCode)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Return request not found: " + returnCode));

        if (returnRequest.getStatus() != ReturnRequestStatus.SUBMITTED) {
            throw new IllegalArgumentException(
                    "Return lines can only be added to submitted requests");
        }

        OrderLine orderLine = orderLineRepository.findById(request.orderLineId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Order line not found: " + request.orderLineId()));

        Order order = returnRequest.getOrder();

        if (!orderLine.getOrder().getId().equals(order.getId())) {
            throw new IllegalArgumentException(
                    "Order line does not belong to the return request's order");
        }

        if (returnLineRepository
                .existsByReturnRequestIdAndOrderLineId(
                        returnRequest.getId(), orderLine.getId())) {
            throw new IllegalArgumentException(
                    "This order line is already included in the return request");
        }

        if (request.quantityRequested() > orderLine.getQuantity()) {
            throw new IllegalArgumentException(
                    "Requested return quantity exceeds the ordered quantity");
        }

        List<ReturnLine> previousReturns =
                returnLineRepository.findAll().stream()
                        .filter(line -> line.getOrderLine().getId()
                                .equals(orderLine.getId()))
                        .toList();

        int previouslyRequested = previousReturns.stream()
                .filter(line -> line.getReturnRequest().getStatus()
                        != ReturnRequestStatus.REJECTED
                        && line.getReturnRequest().getStatus()
                        != ReturnRequestStatus.CLOSED)
                .mapToInt(ReturnLine::getQuantityRequested)
                .sum();

        if (previouslyRequested + request.quantityRequested()
                > orderLine.getQuantity()) {
            throw new IllegalArgumentException(
                    "Requested quantity exceeds the remaining returnable quantity");
        }

        ReturnLine line = new ReturnLine(
                returnRequest,
                orderLine,
                request.quantityRequested()
        );

        return toResponse(returnLineRepository.save(line));
    }

    @Transactional(readOnly = true)
    public List<ReturnLineResponse> getLines(String returnCode) {

        ReturnRequest returnRequest =
                returnRequestRepository.findByReturnCode(returnCode)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Return request not found: " + returnCode));

        return returnLineRepository
                .findByReturnRequest_Id(returnRequest.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ReturnLineResponse toResponse(ReturnLine line) {
        return new ReturnLineResponse(
                line.getId(),
                line.getReturnRequest().getReturnCode(),
                line.getReturnRequest().getOrder().getOrderCode(),
                line.getOrderLine().getLineNumber(),
                line.getOrderLine().getSku(),
                line.getQuantityRequested()
        );
    }
}
