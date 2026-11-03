package com.tecsup.petclinic.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Persistable;
import java.time.LocalDate;

@Entity
@Table(name = "vet_specialties")
@Getter @Setter @NoArgsConstructor
public class VetSpecialty implements Persistable<VetSpecialtyId> {
    @EmbeddedId private VetSpecialtyId id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vet_id", insertable = false, updatable = false)
    private Vet vet;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialty_id", insertable = false, updatable = false)
    private Specialty specialty;
    private LocalDate certificationDate;
    private Integer yearsExperience = 0;
    @Column(name = "is_primary") private boolean primarySpecialty;
    private String notes;
    @Transient private boolean newAssignment = true;

    public VetSpecialty(VetSpecialtyId id) { this.id = id; }

    // Una clave compuesta asignada no significa que la fila ya exista.
    @Override public boolean isNew() { return newAssignment; }
    @PostLoad @PostPersist
    private void markExisting() { newAssignment = false; }
}
