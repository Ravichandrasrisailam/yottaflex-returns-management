
package com.yottaflex.returns_management.inventory;

import com.yottaflex.returns_management.returnrequest.Inspection;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "inventory_dispositions",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_disposition_inspection",
        columnNames = "inspection_id"
    )
)
public class InventoryDisposition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inspection_id", nullable = false, unique = true)
    private Inspection inspection;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "disposition_type", nullable = false, length = 30)
    private DispositionType dispositionType;

    @NotNull
    @Size(max = 500)
    @Column(name = "decision_reason", nullable = false, length = 500)
    private String decisionReason;

    @Column(name = "decided_at", nullable = false, updatable = false)
    private LocalDateTime decidedAt;

    protected InventoryDisposition() {
    }

    public InventoryDisposition(
            Inspection inspection,
            DispositionType dispositionType,
            String decisionReason) {
        this.inspection = inspection;
        this.dispositionType = dispositionType;
        this.decisionReason = decisionReason;
    }

    @PrePersist
    protected void onCreate() {
        if (decidedAt == null) {
            decidedAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public Inspection getInspection() {
        return inspection;
    }

    public DispositionType getDispositionType() {
        return dispositionType;
    }

    public String getDecisionReason() {
        return decisionReason;
    }

    public LocalDateTime getDecidedAt() {
        return decidedAt;
    }
}
