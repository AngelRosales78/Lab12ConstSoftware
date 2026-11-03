package com.tecsup.petclinic.util;

import com.tecsup.petclinic.entities.*;

/** Objetos esperados para la version de pruebas con servicio simulado. */
public final class VetSpecialtyObjectCreator {
    private VetSpecialtyObjectCreator() { }

    public static VetSpecialty assignment(int vetId, int specialtyId) {
        return new VetSpecialty(new VetSpecialtyId(vetId, specialtyId));
    }

    /** Respuesta plana del servicio para los escenarios C+D de Diego. */
    public static VetSpecialty assignment(int vetId, int specialtyId, boolean primary, int years,
            java.time.LocalDate certification) {
        VetSpecialty assignment = assignment(vetId, specialtyId);
        assignment.setPrimarySpecialty(primary);
        assignment.setYearsExperience(years);
        assignment.setCertificationDate(certification);
        return assignment;
    }

    public static Specialty specialty(int id, String name) {
        Specialty specialty = new Specialty();
        specialty.setId(id);
        specialty.setName(name);
        return specialty;
    }

    public static Vet vet(int id, String firstName, String lastName) {
        Vet vet = new Vet();
        vet.setId(id);
        vet.setFirstName(firstName);
        vet.setLastName(lastName);
        return vet;
    }
}
