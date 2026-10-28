
package com.yottaflex.returns_management.refund;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/refunds")
public class RefundController {

    private final RefundService refundService;
    private final RefundRepository refundRepository;

    public RefundController(
            RefundService refundService,
            RefundRepository refundRepository) {
        this.refundService = refundService;
        this.refundRepository = refundRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RefundResponse createRefund(
            @Valid @RequestBody RefundRequest request) {
        return refundService.createRefund(request);
    }

   
@GetMapping("/{refundId}")
public RefundResponse getRefund(@PathVariable Long refundId) {
    Refund refund = refundRepository
            .findByIdWithReturnRequest(refundId)
            .orElseThrow(() ->
                    new IllegalArgumentException("Refund not found"));

    return new RefundResponse(
            refund.getId(),
            refund.getReturnRequest().getId(),
            refund.getReturnRequest().getReturnCode(),
            refund.getAmount(),
            refund.getStatus(),
            refund.getCreatedAt());
}

}
