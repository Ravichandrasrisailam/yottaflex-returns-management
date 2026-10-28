
package com.yottaflex.returns_management.returnrequest;

import com.yottaflex.returns_management.returnrequest.dto.ReturnDecisionRequest;
import com.yottaflex.returns_management.returnrequest.dto.ReturnRequestResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/returns")
public class ReturnDecisionController {

    private final ReturnEligibilityService eligibilityService;

    public ReturnDecisionController(
            ReturnEligibilityService eligibilityService) {
        this.eligibilityService = eligibilityService;
    }

    @PostMapping("/{returnCode}/approve")
    public ReturnRequestResponse approve(
            @PathVariable String returnCode,
            @Valid @RequestBody ReturnDecisionRequest request) {
        return eligibilityService.decide(returnCode, true, request);
    }

    @PostMapping("/{returnCode}/reject")
    public ReturnRequestResponse reject(
            @PathVariable String returnCode,
            @Valid @RequestBody ReturnDecisionRequest request) {
        return eligibilityService.decide(returnCode, false, request);
    }
}
