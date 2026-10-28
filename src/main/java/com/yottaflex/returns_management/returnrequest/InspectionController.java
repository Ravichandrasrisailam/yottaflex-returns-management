
package com.yottaflex.returns_management.returnrequest;

import com.yottaflex.returns_management.returnrequest.dto.CreateInspectionRequest;
import com.yottaflex.returns_management.returnrequest.dto.InspectionResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inspections")
public class InspectionController {

    private final InspectionService inspectionService;

    public InspectionController(InspectionService inspectionService) {
        this.inspectionService = inspectionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InspectionResponse inspect(
            @Valid @RequestBody CreateInspectionRequest request) {
        return inspectionService.inspect(request);
    }

    @GetMapping("/return-line/{returnLineId}")
    public InspectionResponse getByReturnLineId(
            @PathVariable Long returnLineId) {
        return inspectionService.getByReturnLineId(returnLineId);
    }
}
