
package com.yottaflex.returns_management.returnrequest;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "return_inspections")
public class Inspection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "return_line_id",
        nullable = false,
        unique = true
    )
    private ReturnLine returnLine;

    @Enumerated(EnumType.STRING)
    @Column(name = "condition_found", nullable = false, length = 30)
    private InspectionCondition conditionFound;

    @Size(max = 1000)
    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "inspected_at", nullable = false, updatable = false)
    private LocalDateTime inspectedAt;

    protected Inspection() {
    }

    public Inspection(
            ReturnLine returnLine,
            InspectionCondition conditionFound,
            String notes) {
        this.returnLine = returnLine;
        this.conditionFound = conditionFound;
        this.notes = notes;
    }

    @PrePersist
    protected void onCreate() {
        if (inspectedAt == null) {
            inspectedAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public ReturnLine getReturnLine() {
        return returnLine;
    }

    public InspectionCondition getConditionFound() {
        return conditionFound;
    }

    public String getNotes() {
        return notes;
    }

    public LocalDateTime getInspectedAt() {
        return inspectedAt;
    }
}
