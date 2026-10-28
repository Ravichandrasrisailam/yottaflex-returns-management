
package com.yottaflex.returns_management.returnrequest;

import com.yottaflex.returns_management.returnrequest.dto.CreateReturnLineRequest;
import com.yottaflex.returns_management.returnrequest.dto.ReturnLineResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/returns/{returnCode}/lines")
public class ReturnLineController {

    private final ReturnLineService returnLineService;

    public ReturnLineController(ReturnLineService returnLineService) {
        this.returnLineService = returnLineService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReturnLineResponse addLine(
            @PathVariable String returnCode,
            @Valid @RequestBody CreateReturnLineRequest request) {
        return returnLineService.addLine(returnCode, request);
    }

    @GetMapping
    public List<ReturnLineResponse> getLines(
            @PathVariable String returnCode) {
        return returnLineService.getLines(returnCode);
    }
}
