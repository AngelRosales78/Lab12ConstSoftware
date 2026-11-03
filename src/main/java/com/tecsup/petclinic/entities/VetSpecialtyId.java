package com.tecsup.petclinic.entities;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VetSpecialtyId implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column(name = "vet_id") private Integer vetId;
    @Column(name = "specialty_id") private Integer specialtyId;
}
