package com.tecsup.petclinic.services;

import com.tecsup.petclinic.entities.*;
import java.time.LocalDate;
import java.util.List;

public interface VetSpecialtyService {
    VetSpecialty assignSpecialtyToVet(Integer vetId, Integer specialtyId);
    void removeSpecialtyFromVet(Integer vetId, Integer specialtyId);
    List<Specialty> findSpecialtiesByVet(Integer vetId);
    List<Vet> findVetsBySpecialty(Integer specialtyId);
    VetSpecialty setPrimarySpecialty(Integer vetId, Integer specialtyId);
    List<Vet> findVetsByMinExperience(Integer specialtyId, int minimum);
    VetSpecialty updateYearsExperience(Integer vetId, Integer specialtyId, int years);
    VetSpecialty updateCertificationDate(Integer vetId, Integer specialtyId, LocalDate date);
}
