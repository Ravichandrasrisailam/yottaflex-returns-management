
package com.yottaflex.returns_management.returnrequest;
import com.yottaflex.returns_management.returnrequest.dto.ReturnRequestResponse;
import com.yottaflex.returns_management.order.Order;
import com.yottaflex.returns_management.order.OrderStatus;
import com.yottaflex.returns_management.returnrequest.dto.ReturnDecisionRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class ReturnEligibilityService {

    private final ReturnRequestRepository returnRequestRepository;
    private final int policyWindowDays;

    public ReturnEligibilityService(
            ReturnRequestRepository returnRequestRepository,
            @Value("${returns.policy-window-days:30}") int policyWindowDays) {
        this.returnRequestRepository = returnRequestRepository;
        this.policyWindowDays = policyWindowDays;
    }

    @Transactional
    public ReturnRequestResponse decide(
            String returnCode,
            boolean approve,
            ReturnDecisionRequest decision) {

        ReturnRequest request = returnRequestRepository
                .findByReturnCode(returnCode)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Return request not found: " + returnCode));

        if (request.getStatus() != ReturnRequestStatus.SUBMITTED) {
            throw new IllegalArgumentException(
                    "Only submitted requests can be approved or rejected");
        }

        if (request.getReason() == null || request.getReason().isBlank()) {
            throw new IllegalArgumentException(
                    "Return request must have a reason");
        }

        if (approve) {
            validateEligibility(request.getOrder());
            request.setStatus(ReturnRequestStatus.APPROVED);
        } else {
            request.setStatus(ReturnRequestStatus.REJECTED);
        }

        returnRequestRepository.save(request);

        return new ReturnRequestResponse(
                request.getId(),
                request.getReturnCode(),
                request.getOrder().getOrderCode(),
                request.getStatus(),
                request.getReason(),
                request.getRequestedAt()
        );
    }

    private void validateEligibility(Order order) {

        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new IllegalArgumentException(
                    "Only delivered orders are eligible for returns");
        }

        if (order.getDeliveredAt() == null) {
            throw new IllegalArgumentException(
                    "Order delivery date is missing");
        }

        LocalDate deliveredDate = order.getDeliveredAt().toLocalDate();
        LocalDate today = LocalDate.now();

        if (deliveredDate.isAfter(today)) {
            throw new IllegalArgumentException(
                    "Order delivery date cannot be in the future");
        }

        long daysSinceDelivery =
                ChronoUnit.DAYS.between(deliveredDate, today);

        if (daysSinceDelivery > policyWindowDays) {
            throw new IllegalArgumentException(
                    "Return window expired. Allowed window: "
                            + policyWindowDays + " days");
        }
    }
}
