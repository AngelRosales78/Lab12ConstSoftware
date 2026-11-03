package com.tecsup.petclinic.services;

import com.tecsup.petclinic.entities.*;
import com.tecsup.petclinic.exceptions.*;
import com.tecsup.petclinic.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class VetSpecialtyServiceImpl implements VetSpecialtyService {
    private final VetSpecialtyRepository repository;
    private final VetRepository vets;
    private final SpecialtyRepository specialties;

    private VetSpecialtyId key(Integer vetId, Integer specialtyId) {
        if (vetId == null || specialtyId == null) {
            throw new IllegalArgumentException("Se requieren veterinario y especialidad");
        }
        return new VetSpecialtyId(vetId, specialtyId);
    }

    private void lockVet(Integer vetId) {
        if (vetId == null) throw new IllegalArgumentException("Se requiere veterinario");
        // Serializa cambios del mismo veterinario, incluida la eleccion de principal.
        vets.findByIdForUpdate(vetId).orElseThrow(() ->
                new IllegalArgumentException("Veterinario no encontrado: " + vetId));
    }

    private VetSpecialty assigned(VetSpecialtyId id) {
        return repository.findById(id).orElseThrow(() -> new VetSpecialtyNotFoundException(id));
    }

    @Override
    public VetSpecialty assignSpecialtyToVet(Integer vetId, Integer specialtyId) {
        VetSpecialtyId id = key(vetId, specialtyId);
        lockVet(vetId);
        if (!specialties.existsById(specialtyId)) {
            throw new IllegalArgumentException("Especialidad no encontrada: " + specialtyId);
        }
        if (repository.existsById(id)) throw new DuplicateVetSpecialtyException(id);
        return repository.saveAndFlush(new VetSpecialty(id));
    }

    @Override
    public void removeSpecialtyFromVet(Integer vetId, Integer specialtyId) {
        VetSpecialtyId id = key(vetId, specialtyId);
        lockVet(vetId);
        repository.delete(assigned(id));
    }

    @Override @Transactional(readOnly = true)
    public List<Specialty> findSpecialtiesByVet(Integer vetId) {
        if (vetId == null) throw new IllegalArgumentException("Se requiere veterinario");
        return repository.findSpecialtiesByVet(vetId);
    }

    @Override @Transactional(readOnly = true)
    public List<Vet> findVetsBySpecialty(Integer specialtyId) {
        if (specialtyId == null) throw new IllegalArgumentException("Se requiere especialidad");
        return repository.findVetsBySpecialty(specialtyId);
    }

    @Override
    public VetSpecialty setPrimarySpecialty(Integer vetId, Integer specialtyId) {
        VetSpecialtyId id = key(vetId, specialtyId);
        lockVet(vetId);
        VetSpecialty target = assigned(id);
        List<VetSpecialty> assignments = repository.findByIdVetId(vetId);
        for (VetSpecialty assignment : assignments) {
            assignment.setPrimarySpecialty(assignment.getId().equals(id));
        }
        repository.saveAll(assignments);
        return target;
    }

    @Override @Transactional(readOnly = true)
    public List<Vet> findVetsByMinExperience(Integer specialtyId, int minimum) {
        if (specialtyId == null || minimum < 0) {
            throw new IllegalArgumentException("Especialidad requerida y experiencia no negativa");
        }
        return repository.findVetsByMinExperience(specialtyId, minimum);
    }

    @Override
    public VetSpecialty updateYearsExperience(Integer vetId, Integer specialtyId, int years) {
        if (years < 0) throw new IllegalArgumentException("La experiencia no puede ser negativa");
        VetSpecialtyId id = key(vetId, specialtyId);
        lockVet(vetId);
        VetSpecialty assignment = assigned(id);
        assignment.setYearsExperience(years);
        return repository.save(assignment);
    }

    @Override
    public VetSpecialty updateCertificationDate(Integer vetId, Integer specialtyId, LocalDate date) {
        if (date == null) throw new IllegalArgumentException("Se requiere fecha de certificacion");
        VetSpecialtyId id = key(vetId, specialtyId);
        lockVet(vetId);
        VetSpecialty assignment = assigned(id);
        assignment.setCertificationDate(date);
        return repository.save(assignment);
    }
}
