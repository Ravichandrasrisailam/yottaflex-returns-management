
package com.yottaflex.returns_management.returnrequest;

import com.yottaflex.returns_management.returnrequest.dto.CreateReturnRequest;
import com.yottaflex.returns_management.returnrequest.dto.ReturnRequestResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/returns")
public class ReturnRequestController {

    private final ReturnRequestService returnRequestService;

    public ReturnRequestController(
            ReturnRequestService returnRequestService) {
        this.returnRequestService = returnRequestService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReturnRequestResponse create(
            @Valid @RequestBody CreateReturnRequest request) {
        return returnRequestService.create(request);
    }

    @GetMapping("/{returnCode}")
    public ReturnRequestResponse getByCode(
            @PathVariable String returnCode) {
        return returnRequestService.getByCode(returnCode);
    }

    @PostMapping("/{returnCode}/inspected")
    public ReturnRequestResponse markInspected(
            @PathVariable String returnCode) {
        return returnRequestService.markInspected(returnCode);
    }
}
