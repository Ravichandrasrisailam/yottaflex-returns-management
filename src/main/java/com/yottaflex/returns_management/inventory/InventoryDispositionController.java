
package com.yottaflex.returns_management.inventory;

import com.yottaflex.returns_management.inventory.dto.CreateDispositionRequest;
import com.yottaflex.returns_management.inventory.dto.InventoryDispositionResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory/dispositions")
public class InventoryDispositionController {

    private final InventoryDispositionService dispositionService;

    public InventoryDispositionController(
            InventoryDispositionService dispositionService) {
        this.dispositionService = dispositionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InventoryDispositionResponse create(
            @Valid @RequestBody CreateDispositionRequest request) {
        return dispositionService.create(request);
    }

    @GetMapping("/inspection/{inspectionId}")
    public InventoryDispositionResponse getByInspectionId(
            @PathVariable Long inspectionId) {
        return dispositionService.getByInspectionId(inspectionId);
    }
}
