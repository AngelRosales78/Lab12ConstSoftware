package com.tecsup.petclinic.dtos;

import com.tecsup.petclinic.entities.VetSpecialty;
import java.time.LocalDate;

/** Contrato JSON plano: no expone proxies JPA ni objetos relacionados. */
public record VetSpecialtyDTO(Integer vetId, Integer specialtyId,
        LocalDate certificationDate, Integer yearsExperience, boolean primarySpecialty, String notes) {
    public static VetSpecialtyDTO from(VetSpecialty assignment) {
        return new VetSpecialtyDTO(assignment.getId().getVetId(), assignment.getId().getSpecialtyId(),
                assignment.getCertificationDate(), assignment.getYearsExperience(),
                assignment.isPrimarySpecialty(), assignment.getNotes());
    }
}
